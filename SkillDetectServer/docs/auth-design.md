# 鉴权设计文档（待实现，不在当前代码中启用）

> 状态：**仅设计**。当前 `security.enabled=false`，HMI/M2M 均不鉴权。
> 后续按实际 SSO 提供方与流水线接入方式落地实现，对外接口 URL / 请求体保持不变。

## 1. 目标

- **HMI（`/api/v1`）**：接入公司统一 SSO（OIDC/OAuth2），识别登录用户，做任务级归属与审计。
- **M2M（`/api/m2m/v1`）**：API Key 鉴权，供流水线机器调用，支持 scope 与过期。
- **引擎内部**（Server ↔ Engine）：可选共享 token（`X-Engine-Token`），引擎仅监听内网。

## 2. 启用开关

| 配置 | 默认 | 说明 |
|---|---|---|
| `security.enabled` | `false` | 全局鉴权开关 |
| `security.hmi.enabled` | `false` | HMI SSO 开关 |
| `security.m2m.enabled` | `false` | M2M API Key 开关 |

`false` 时注册 `NoopAuthenticator`（放行 + 匿名 principal），接口行为与现在完全一致。

## 3. HMI SSO（OIDC）

- 方案：`spring-boot-starter-oauth2-resource-server` + JWT（`Authorization: Bearer <token>`）。
- 配置点：`spring.security.oauth2.resourceserver.jwt.issuer-uri`、`jwk-set-uri`、audience 校验。
- 过滤器链：
  - `/api/v1/**` → JWT 校验 + 提取 `sub`/组信息写入 `SecurityContext`；
  - `/healthz` `/readyz` `/actuator/**` → 放行（运维探活）；
  - `/api/m2m/v1/**` → 走 M2M API Key 过滤器，不走 JWT。
- 用户身份贯穿：`ScanService.create()` 将 `principal` 写入 `scan_task.created_by`；列表查询按 `created_by` 过滤。

## 4. M2M API Key

- 方案：自定义 `OncePerRequestFilter`，校验请求头 `X-API-Key`。
- 密钥存储：`api_credential` 表（只存哈希，不存明文）：
  - `key_hash = sha256(secret)`，查询按 hash 匹配；
  - `scopes JSON`（默认 `[]`，如 `["scan:create", "scan:read"]`）；
  - `enabled`、`expires_at`。
- 校验流程：取头 → 归一化 → hash → 查表 → 校验启用/过期/scope → 写入 `SecurityContext`。
- 密钥管理：提供 `/api/m2m/v1/credentials`（或运维接口）创建/吊销 key；创建时明文只返回一次。

## 5. 引擎内部 token（可选）

- Server 调用引擎时附带 `X-Engine-Token`，引擎在 `FastAPI` 中间件校验 `ENGINE_TOKEN` 环境变量。
- 引擎监听地址保持内网/Compose 网络，默认不暴露公网。

## 6. 落地顺序建议

1. 接入 SSO 的 issuer/JWKS，确认 `sub`、组属性字段；
2. 实现 M2M API Key filter + `api_credential` 增删查；
3. 打开 `security.m2m.enabled=true` 灰度；
4. 打开 `security.hmi.enabled=true`，前端同步带上 Bearer token；
5. 补审计：登录用户、key id、IP、请求时间写入访问日志。

## 7. 非目标（当前不实现）

- 不做细粒度 RBAC 权限模型（先只区分 HMI 用户 / M2M key 两类）。
- 不做多租户隔离（P3 再评估）。
