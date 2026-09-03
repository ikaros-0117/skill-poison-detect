# 二次开发指南

## 1. 环境要求

| 工具 | 版本 | 用途 |
|---|---|---|
| JDK | 17 | 编译/运行 Server |
| Maven | 3.9+ | Server 构建（可用 `maven-settings.xml` 镜像） |
| Docker + Compose | 任意较新版本 | 运行整套服务 |
| Python | 3.12（引擎镜像内） | 引擎封装/SkillSpector |

## 2. 常用命令

```bash
# ---- Server 本地编译/打包（需要联网拉 Maven 依赖）----
cd SkillDetectServer
mvn -s maven-settings.xml -q -Dmaven.test.skip=true compile
mvn -s maven-settings.xml -q -Dmaven.test.skip=true package

# ---- 整体构建/启动 ----
docker compose up --build -d
docker compose ps
docker compose logs -f server engine

# ---- 停止 / 清理 ----
docker compose down          # 停止并删除容器（保留数据卷）
docker compose down -v       # 连数据卷一起删除
```

> `maven-settings.xml` 使用阿里云镜像；若你的网络能直连 Maven Central，可去掉 `-s maven-settings.xml`。

## 3. 新增一个 HMI/M2M 接口

1. 在 `scan/controller` 下新建控制器，或扩展 `HmiScanController` / `M2mScanController`。
2. 业务编排放 `ScanService`（或新建 `@Service`），状态迁移/落库放 `ScanStateService`（`@Transactional`）。
3. 返回统一包裹：`ApiResponse.ok(data)`；业务错误抛 `BusinessException(code, msg)`，由 `GlobalExceptionHandler` 统一转响应。
4. 同步更新 `SkillDetectServer/docs/openapi.yaml`。

## 4. 新增/修改一条检测规则

检测规则由 **SkillSpector** 引擎负责，不在 Server 内定义：

1. 修改 SkillSpector 分析器（`SkillSpector/src/skillspector/nodes/analyzers/...`）。
2. 若规则目录需要展示，同步更新 `SkillDetectServer/src/main/resources/rules.json`。
   - 重新生成命令（在 `SkillSpector` 目录，用 Python 解析 README 表格）：
     ```bash
     python3 - <<'PY'
     # 见 git 历史中 rules.json 的生成脚本，或手工维护 rules.json
     PY
     ```
   - 或直接手工编辑 `rules.json`。
3. 重新构建引擎镜像与 Server 镜像。

## 5. 修改引擎薄封装

- 文件：`skillspector-engine/app.py`。
- 新增输入字段：改 `ScanRequest`（Pydantic），在 `_run_scan` 中把字段写进 `state`。
- 新增接口：直接加 FastAPI 路由。
- 重新构建：`docker compose up --build -d`（`COPY skillspector-engine` 会触发重建，SkillSpector 层有缓存）。

> 若需要引擎侧也变成“提交 + 轮询”异步模型：把 `_run_scan` 放入 `asyncio.create_task`，
> 新增 `GET /v1/scan/{id}` 查询 `_active_tasks` 状态与结果；Server 的 `EngineClient` 改为短轮询即可。

## 6. 切换 Redis 队列到 Stream（P1 可选）

只改 `ScanQueueService` 内部实现，保持对外方法签名不变：

- `enqueue` → `XADD`
- `blockingPop` → `XREADGROUP`
- 完成/失败 → `XACK`
- 超时重投 → `XPENDING` / `XAUTOCLAIM`

上层 `ScanDispatcher` / `ScanReconciler` / 控制器无需改动。契约见 `SkillDetectServer/docs/redis-queue-contract.md`。

## 7. 接入鉴权（按 auth-design.md）

1. 添加依赖：`spring-boot-starter-oauth2-resource-server`（HMI）。
2. 实现 `OncePerRequestFilter`（M2M API Key）。
3. 用 `security.enabled` / `security.hmi.enabled` / `security.m2m.enabled` 开关化；默认 `false` 走 `NoopAuthenticator`。
4. HMI 用户身份写入 `scan_task.created_by`，列表按 `created_by` 过滤。

## 8. 配置项

完整配置见 `application.yml`。常用：

```yaml
scan:
  risk-threshold: 50
  concurrency.max-active: 8
  engine:
    base-url: http://localhost:8000
    timeout-seconds: 720
    circuit-failure-threshold: 5
    circuit-open-seconds: 60
  storage.base-dir: /data
  retention-days: 30

rate-limit:
  enabled: true
  window-seconds: 60
  hmi-requests-per-minute: 120
  m2m-requests-per-minute: 60

security:
  enabled: false
```

## 9. 排障

| 现象 | 排查 |
|---|---|
| Server 启动失败连不上 DB | 确认 mysql healthy、`SPRING_DATASOURCE_URL` |
| 任务卡 `QUEUED` | 看 `ScanDispatcher`/`ScanReconciler` 日志；检查 Redis `LLEN skillscan:queue` |
| 扫描返回 `FAILED` | 看 `scan_task.error_msg` + `docker compose logs server` + engine 日志 |
| `llm_available=false` | 检查 `.env` 的 `OPENAI_BASE_URL`/`OPENAI_API_KEY`，重建 engine 后 `curl :8000/health` |
| Maven 拉取失败 | 使用 `-s maven-settings.xml` 镜像，或配置你自己的镜像 |
| Docker Hub 拉镜像失败 | 配置 Docker registry mirror（如 OrbStack 的镜像加速） |
