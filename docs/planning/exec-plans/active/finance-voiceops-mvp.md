# Finance VoiceOps MVP

## 제목

FlowMind Finance VoiceOps MVP 구현 계획

## 목표

- 금융 본인확인·거래안내 중심의 하이브리드 대화 엔진 MVP를 완성한다.

## 범위

- 도메인 주제 확정: `FlowMind Finance VoiceOps`
- Intent 3개 구현
  - `ACCOUNT_INFO_INQUIRY`
  - `TRANSACTION_HISTORY_INQUIRY`
  - `CARD_LOST_OR_LIMIT`
- slot schema 및 session context 구조 확정
- `POST /api/dispatch` MVP 구현
- deterministic scenario 3개 구현
- LLM fallback 조건 구현
  - intent confidence 낮음
  - 복합 요청
  - 감정/불만 표현 중심 요청
- fallback reason/trace/log 저장
- 개선 리포트 지표 수집
  - 오분류율
  - fallback 비율
  - 응답시간

## 비범위

- full RAG 도입
- 멀티 모델 provider 라우팅
- 대규모 프론트엔드 제품 UI
- 실시간 음성 스트리밍 엔진

## 예상 변경 파일

- 생성 파일
  - `backend/src/main/java/com/flowmind/dispatch/**`
  - `backend/src/main/java/com/flowmind/intent/**`
  - `backend/src/main/java/com/flowmind/scenario/**`
  - `backend/src/main/java/com/flowmind/session/**`
  - `backend/src/main/resources/prompts/**`
  - `backend/src/main/resources/db/migration/V2__*.sql` 이후 migration
- 수정 파일
  - `docs/planning/PHASES.md`
  - `docs/planning/phases/phase-2/plan.md`
  - `docs/planning/phases/phase-2/design.md`
  - `docs/planning/phases/phase-3/plan.md`
  - `docs/planning/phases/phase-3/design.md`
  - `docs/planning/phase-roadmap.md`
  - `backend/src/main/resources/application.yml`
- 참고 파일
  - `docs/guides/conversation-system-breakdown.md`
  - `docs/operations/RELIABILITY.md`
  - `docs/operations/SECURITY.md`

## 리스크

- 금융 인증/민감정보 처리 범위를 MVP에서 과소 정의할 위험
- fallback 기준이 느슨하면 LLM 호출이 과다해질 위험
- scenario와 fallback 경계가 모호하면 운영 지표 해석이 어려울 위험

## 작업 단계

1. 금융 VoiceOps 도메인 모델/시나리오 상세 설계 확정
2. Intent/Entity/Slot/SessionContext 모델 구현
3. Dispatch API + deterministic scenario 3개 구현
4. LLM fallback 조건 및 reason/trace/log 저장 구현
5. 지표 수집, 테스트, 문서 정리

## 검증 기준

- 성공 조건
  - 3개 금융 시나리오가 deterministic 경로로 동작
  - 누락 슬롯 후속 질문 동작
  - fallback 조건 3종이 정확히 라우팅
  - trace/log/지표가 저장
- 실패 조건
  - intent 불일치로 오동작
  - 슬롯 누락 상황에서 대화가 중단
  - fallback reason 누락
  - 민감정보가 마스킹 없이 로그에 저장
- 확인 명령 또는 테스트 방법
  - `.\gradlew.bat test`
  - `Invoke-WebRequest http://localhost:8080/api/health`
  - dispatch API 시나리오별 통합 테스트

## 진행 상태

- `in_progress`
- 2026-04-28 업데이트:
  - `FLOWMIND_CONFIDENCE_THRESHOLD` 설정 기반 confidence gate 적용
  - fallback reason 코드 분리(`LOW_CONFIDENCE`, `COMPLEX_REQUEST`, `EMOTION_HEAVY`)
  - dispatch trace에 `latencyMs` 필드 추가
  - metrics에 fallback reason별 카운트 포함
  - 운영 디버깅용 `GET /api/dispatch/traces?limit=N` 추가
  - traces 조회 limit 가드레일 적용(1~100 범위 보정)
