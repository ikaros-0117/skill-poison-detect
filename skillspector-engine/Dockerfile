# Build context: repository root (so both SkillSpector/ and skillspector-engine/ are visible).
#   docker build -f skillspector-engine/Dockerfile -t skillspector-engine .
FROM python:3.12-slim-bookworm

ARG PIP_INDEX_URL=https://pypi.tuna.tsinghua.edu.cn/simple

WORKDIR /app

# Install SkillSpector (and its dependencies) from the local source tree.
COPY SkillSpector /app/skillspector-src
RUN pip install --no-cache-dir -i ${PIP_INDEX_URL} /app/skillspector-src

# Install engine wrapper dependencies.
COPY skillspector-engine /app/engine
RUN pip install --no-cache-dir -i ${PIP_INDEX_URL} fastapi "uvicorn[standard]"

ENV PYTHONUNBUFFERED=1
WORKDIR /app/engine

EXPOSE 8000
CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8000"]
