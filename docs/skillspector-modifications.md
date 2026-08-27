# SkillSpector 本地改动说明（vendored 源码）

本项目以源码形式随仓库分发 `SkillSpector/`，引擎镜像构建时 `pip install` 该目录。
以下是对 SkillSpector 源码所做的本地调整。

## 1. 调大工作流时间预算（已提交）

**文件**：`SkillSpector/src/skillspector/state.py`

**改动**：

```python
MAX_WORKFLOW_SECONDS = 60.0   # 修改前
MAX_WORKFLOW_SECONDS = 600.0  # 修改后
```

**原因**：

- SkillSpector 默认对整个扫描工作流设了 60 秒的聚合运行预算。
- 启用 LLM 后，一次扫描会产生多次 LLM 调用（3 个语义分析器 + `meta_analyzer` 按文件分 batch）。
  对含多个文件的 skill，累计耗时容易超过 60 秒。
- 超时点通常落在最后执行的 `meta_analyzer`，表现为：
  - `meta_analyzer` 状态 `degraded`（`completed:1, partial:1`）；
  - 报告 `analysis_completeness.is_complete=false`；
  - `limitations: ["Analyzer meta_analyzer status: degraded."]`；
  - `ledger_exceptions` 中出现 `LLMRuntimeLimitError` / `reason_code=runtime_limit`。
- 本项目设计单次扫描上限为 10 分钟，Server 侧引擎调用超时为 720 秒，
  因此将预算提升到 600 秒（< 720s，保证引擎先给出干净结论）。

## 2. 新增模型 gpt-5.6-sol 的 token 预算（仅本地，不提交）

**文件**：`SkillSpector/src/skillspector/providers/openai/model_registry.yaml`

**改动**（仅保留在本地工作区，不进 Git）：

```yaml
"gpt-5.6-sol":
  context_length: 1000000
  max_output_tokens: 128000
```

**原因与说明**：

- 该模型是内部 OpenAI 兼容网关的模型名，网关/模型参数为部署环境特定信息；
- 当前 `context_length` / `max_output_tokens` 为按 gpt-5.x 同类模型的**占位值**，待确认真实参数；
- 因此按约定**不提交到仓库**，只保留在本地工作区，避免把内部模型配置带入公共仓库。

> 若后续确认了 `gpt-5.6-sol` 的真实 token 参数并希望入库，可再决定是否提交。
