# 实现说明

## 1. 已实现功能总览

| 阶段 | 内容 | 状态 |
|---|---|---|
| 方案 | 架构/接口/数据/任务/健康/安全/部署/分期 | ✅ 已定稿 |
| 可评审物 | OpenAPI、DDL、Redis 契约、引擎薄封装 | ✅ |
| P0 | Spring Boot 工程、异步扫描、HMI/M2M、健康、SARIF | ✅ 已验证 |
| P1 | LLM 接入、规则目录、重试、30 天留存 | ✅ 已验证（重试守卫） |
| P2 | 基线、限流、熔断、监控、深探健康 | ✅ 已实现/验证 |
| P2 鉴权 | 设计文档（不实现） | ✅ 仅 `auth-design.md` |

## 2. Server 模块划分（`SkillDetectServer/src/main/java/com/skilldetect/server/`）

| 包 | 职责 | 关键类 |
|---|---|---|
| `common` | 响应包裹/异常 | `ApiResponse`、`BusinessException`、`GlobalExceptionHandler` |
| `config` | 配置/指标/限流/Web | `ScanProperties`、`RateLimitProperties`、`ScanMetrics`、`RateLimitInterceptor`、`WebConfig`、`AppConfig` |
| `engine` | 引擎客户端 | `EngineClient`、`EngineScanResponse`、`EngineCircuitBreaker`、`EngineCircuitOpenException` |
| `health` | 健康 | `HealthController`、`EngineHealthLogEntity/Repository`、`EngineHealthProbe` |
| `scan/domain` | 实体 | `ScanTaskEntity`、`ScanFindingEntity`、`ScanBaselineEntity`、`ScanStatus` |
| `scan/repository` | 数据访问 | `ScanTaskRepository`、`ScanFindingRepository`、`ScanBaselineRepository` |
| `scan/queue` | 队列 | `ScanQueueService` |
| `scan/service` | 编排/状态/文件 | `ScanService`、`ScanExecutionService`、`ScanStateService`、`ScanReconciler`、`ScanRetentionCleaner`、`FileStorageService` |
| `scan/dispatcher` | 调度 | `ScanDispatcher` |
| `scan/controller` | HMI/M2M | `HmiScanController`、`M2mScanController`、`ScanDtos` |
| `scan/rules` | 规则目录 | `RulesController`（加载 `rules.json`） |
| `scan/baseline` | 基线 | `BaselineController` |

## 3. 引擎薄封装（`skillspector-engine/`）

- `app.py`：
  - `GET /health`：版本/provider/model/LLM 可用性/活跃数/统计。
  - `GET /health/deep`：静态扫内置 fixture，端到端探活。
  - `POST /v1/scan`：同步扫描，支持 `path` / `use_llm` / `output_format` / `baseline`；`asyncio.Semaphore` 限并发。
  - `POST /v1/scan/cancel`：尽力取消（同步模式下仅登记，结果由 Server 守卫忽略）。
- 核心：直接调用 `skillspector.graph.ainvoke`，用 `load_baseline` + `effective_findings` 应用基线，返回与 `run_scan` 一致的 verdict。

## 4. 数据模型（PostgreSQL，事实来源）

- `scan_task`：任务主表（状态机 + 结论摘要 + 报告路径 + `metadata` JSONB + `baseline_id`）。
- `scan_finding`：发现项（`task_id`、`rule_id`、`severity`、位置、说明、指纹）。
- `scan_baseline`：基线（YAML/JSON 内容）。
- `api_credential`：预留（鉴权）。
- `engine_health_log`：深探健康历史。

完整 DDL 见 `SkillDetectServer/docs/schema.sql`（`ddl-auto=update` 仅用于开发，生产建议 Flyway 对齐）。

## 5. 开发过程中修复的关键问题

1. **入队时机竞态**：最初 `create()` 在事务提交前入队，worker 取出后查不到未提交记录 → 任务卡 `QUEUED`。
   修复：改为“先落库提交、再入队”，并加周期自愈补队列。
2. **队列一致性**：早期 `queued-set` 与 List 可能失同步导致自愈误判。
   修复：去掉 `queued-set`，直接用 `LRANGE` 成员关系做去重判断。
3. **Spring 6 包路径**：`MissingServletRequestPartException` 位于 `org.springframework.web.multipart.support`。
4. **Dockerfile ARG 作用域**：`ARG` 声明在 `FROM` 前在 `RUN` 中不展开，需 `FROM` 后重声明。
5. **构建网络**：Maven Central TLS 握手失败 → 使用阿里云镜像（`maven-settings.xml`）；引擎 pip 使用清华 PyPI 镜像。

## 6. 已验证的端到端行为

- HMI 上传 zip → `SUCCEEDED`、`riskScore=0`、`findings=[]`（良性样本）。
- M2M 上传 zip → `SUCCEEDED`、`sarifUrl` 可下载、metadata 透传。
- LLM：`useLlm=true` → `scanMode=static+llm`、`llmUsed=true`。
- 规则目录：`/api/v1/rules` 返回 17 类 / 72 条。
- 基线：创建/列表可用；`baselineId` 扫描透传引擎。
- 监控：`/actuator/prometheus` 暴露 `skillscan_queue_depth`。
- 深探健康：`engine_health_log` 定时写入，状态 UP。

## 7. 尚未运行时验证的项

- 限流触发 429、熔断开路→半开→恢复的完整流转（代码已就绪）。
- 留存清理定时任务的实际删除效果（依赖时间条件）。
- 重试的实际执行链路（仅验证了状态守卫）。
