# FlowMind Security

## 목적

이 문서는 FlowMind MVP에서 반드시 지켜야 할 최소 보안 원칙을 정리한다.

## 핵심 원칙

- 민감 정보는 최소한으로 다룬다
- prompt에 raw PII를 그대로 넣지 않는다
- 로그와 trace에도 민감 정보 노출을 줄인다
- 외부 API 호출은 중앙 게이트웨이에서 통제한다

## PII 처리 원칙

- 전화번호, 계정번호, 인증 관련 값은 masking 우선
- prompt augmentation 시 필요한 최소 정보만 포함
- session context 저장 시 노출 범위를 최소화

## LLM 연동 원칙

- LLM 호출은 `LlmGateway` 한 곳에서만 수행
- 모델명, timeout, retry 정책은 설정으로 관리
- fallback input에는 필요한 컨텍스트만 전달

## 로그 원칙

- `dispatch_trace`에는 판단 근거를 남기되 원문 민감정보는 최소화
- `conversation_log`는 운영 분석 기준으로 설계하되 개인정보 보관 범위를 제한

## MVP 보안 체크

- API key는 환경 변수 사용
- raw prompt와 response 저장 범위 검토
- masking 여부 검증
- 외부 연동 시 mock과 production 경계 분리
