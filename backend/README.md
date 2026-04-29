# FlowMind Backend

Phase 1 bootstrap for the FlowMind backend.

## Requirements

- Java 21
- Gradle 8.x or the generated Gradle wrapper

## Run

```powershell
cd backend
.\gradlew.bat bootRun
```

## Run (Free Local AI Stack)

```powershell
# repo root
docker compose -f docker-compose.local-ai.yml up -d

# 모델 다운로드(최초 1회)
docker exec -it flowmind-ollama ollama pull qwen2.5:7b
docker exec -it flowmind-ollama ollama pull nomic-embed-text

# backend 실행
cd backend
.\gradlew.bat bootRun --args="--spring.profiles.active=local"
```

## Verify

```powershell
Invoke-WebRequest http://localhost:8080/api/health
Invoke-WebRequest http://localhost:8080/actuator/health
Invoke-WebRequest http://localhost:8080/api/dispatch/metrics
Invoke-WebRequest "http://localhost:8080/api/dispatch/traces?limit=10"
Invoke-WebRequest http://localhost:8080/api/ai/chat -Method Post -ContentType "application/json" -Body '{"message":"안녕하세요","sessionId":"s-1"}'
Invoke-WebRequest http://localhost:8080/swagger-ui.html
.\gradlew.bat test
.\\gradlew.bat spotlessCheck
```

## Environment variables

- `FLOWMIND_SERVER_PORT`
- `FLOWMIND_DB_URL`
- `FLOWMIND_DB_USERNAME`
- `FLOWMIND_DB_PASSWORD`
- `FLOWMIND_DB_ENABLED`
- `FLOWMIND_FLYWAY_ENABLED`
- `FLOWMIND_REDIS_ENABLED`
- `FLOWMIND_OPENAI_ENABLED`
- `FLOWMIND_SWAGGER_ENABLED`
- `FLOWMIND_CONFIDENCE_THRESHOLD` (default: `0.65`)
- `FLOWMIND_OLLAMA_BASE_URL` (default: `http://localhost:11434`)
- `FLOWMIND_OLLAMA_CHAT_MODEL` (default: `qwen2.5:7b`)
- `FLOWMIND_OLLAMA_EMBEDDING_MODEL` (default: `nomic-embed-text`)

The bootstrap defaults are set to start without requiring a live database connection.

`/api/dispatch/traces` limit은 1~100 범위로 안전 보정된다.
`/api/dispatch/metrics`는 `fallbackReasonCounts`, `fallbackReasonRates`, `fallbackReasonRatesWithinFallback`를 함께 제공한다.
`/api/dispatch/metrics?window=N`은 최근 N요청 추세 지표를 제공하며 N은 1~200으로 보정된다.
최근 구간 요약으로 `recentFallbackCount`, `recentFallbackRate`, `recentFallbackLatencyAvgMs`도 함께 제공된다.
`/api/ai/chat` 응답에는 `latencyMs`, `sessionId`가 포함된다.
`/api/ai/chat` 오류 응답에는 `errorCode`(`INVALID_REQUEST`, `LLM_UNAVAILABLE`)가 포함된다.
`/api/ai/chat` 오류 응답에는 `timestamp`(ISO-8601)도 포함된다.
