# Redis 任务队列契约（SkillDetectServer）

Redis 仅作为**派发通道与短期状态**；PostgreSQL 是任务事实来源，Redis 数据可重建。

> 版本约定：**P0 使用 Redis List**（单消费者、实现简单）；**P1 可选升级 Redis Stream**
> （多消费者 / 显式 ack / 自动重投）。两者对 Server 上层透明，只影响 `ScanQueueService` 内部实现。

## 1. P0 数据结构（List）

| Key / 类型 | 用途 |
|---|---|
| `skillscan:queue` (List) | 待扫描任务队列，元素为 `taskNo` 字符串；去重判断直接用 `LRANGE` 成员关系 |
| `skillscan:cancelled` (Set, TTL 24h) | 待取消/已取消的 taskNo，消费前跳过 |
| `skillscan:active` (String, INCR/DECR) | 预留：全局执行并发计数（单实例用线程数即可） |

> 队列元素只存 `taskNo`；`useLlm`、`riskThreshold`、`reportFormat` 等字段以 `scan_task` 为准，
> 派发时从 PostgreSQL 读取，避免双写不一致。

## 2. 入队 / 消费协议

入队（创建任务时）：

```redis
LPUSH skillscan:queue <taskNo>
```

消费循环（Server 内 `max-active` 个 worker 线程，每线程串行）：

```redis
BRPOP skillscan:queue 5        # 阻塞 5s，超时返回 null
```

处理流程：

1. 取到 `taskNo` 后，读 `scan_task`；状态非 `QUEUED` 则跳过。
2. 若 `taskNo` 在 `skillscan:cancelled`，置 `CANCELED` 并返回，不派发。
3. 原子抢占：`UPDATE scan_task SET status='RUNNING', started_at=now() WHERE task_no=? AND status='QUEUED'`；影响行数为 0 表示已被其他 worker 抢占，跳过。
4. 调用引擎 `POST /v1/scan`（同步，读超时 12min）。
5. 成功：写 DB `SUCCEEDED` + 结果摘要 + 发现项；失败：写 DB `FAILED`（执行型错误不自动重试）。

## 3. 并发控制

- `scan.concurrency.max-active`（默认 8，可配置）= **worker 线程数**，每个线程一次只处理一个任务。
- 其余任务留在 `skillscan:queue` 中排队；队列深度 `LLEN skillscan:queue`。
- 引擎侧另有 `ENGINE_MAX_CONCURRENT_SCANS=8` 作为第二道保护，二者取较小值生效。

## 4. 取消

- `QUEUED`：`LREM skillscan:queue 0 <taskNo>`，DB 置 `CANCELED`。
- `RUNNING`：DB 置 `CANCELED`（同步引擎接口无法中途强杀，引擎返回结果后被 `status != RUNNING` 守卫忽略）。
- 消费前统一检查 `skillscan:cancelled`，保证竞态下已取消任务不被派发。

## 5. 超时 / 失败 / 重试

- 引擎调用超时 `engine.timeout-seconds=720`（12min，> 单次 10min 上限）。
- `ScanReconciler` 每 30s 扫描 `RUNNING` 且 `started_at + (timeout+120s) < now()` 的任务，置 `FAILED(TIMEOUT)`。
- 仅对“引擎未真正执行 / 网络类”错误按 `retry_count < 3` 重新入队，执行型错误直接 `FAILED`。

## 6. 重启恢复（Redis 丢数据兜底）

Server 启动时（`ApplicationReadyEvent`）对账：

1. 查 PostgreSQL 中 `status='QUEUED'` 的任务。
2. 对不在 `skillscan:queue` 列表中的任务（`LRANGE` 判断成员关系），重新 `LPUSH`。
3. 因原子抢占去重，即使偶发重复入队也不会重复执行。

## 7. P1 可选升级：Redis Stream

当需要多 Server 实例并行消费或更严格的 ack/重投语义时，将 `ScanQueueService` 内部切换为 Stream：

| P0 (List) | P1 (Stream) |
|---|---|
| `LPUSH skillscan:queue` | `XADD skillscan:queue * taskNo ...` |
| `BRPOP` | `XREADGROUP GROUP skillscan:dispatcher consumer-<id> COUNT 1 BLOCK 5000 STREAMS skillscan:queue >` |
| 无显式 ack | `XACK` |
| 对账靠 DB + LRANGE 成员关系 | `XPENDING` / `XAUTOCLAIM` 自动重投 |

消息体与 `ScanQueueService` 对上层暴露的方法签名保持一致，控制器/调度器无需改动。

## 8. 可观测指标

| 指标 | 来源 | 说明 |
|---|---|---|
| 队列深度 | `LLEN skillscan:queue` | 待派发任务数 |
| 处理中任务 | DB `status='RUNNING'` | 当前执行并发 |
| 积压/等待时间 | `scan_task.created_at` 与 `started_at` 差 | 队列等待时长 |
