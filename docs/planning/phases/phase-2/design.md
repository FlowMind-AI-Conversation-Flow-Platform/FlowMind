# Phase 2 Design

## 목적

규칙 기반 대화 처리의 최소 구조를 정의한다.

## 핵심 컴포넌트

- `IntentCatalog`
- `EntityExtractor`
- `SlotFiller`
- `SessionContextService`
- `ScenarioExecutor`
- `DispatchController`

## 첫 구현 대상

- `ACCOUNT_INFO_INQUIRY`
- `TRANSACTION_HISTORY_INQUIRY`
- `CARD_LOST_OR_LIMIT`

이 중 `ACCOUNT_INFO_INQUIRY`를 첫 end-to-end 시나리오로 잡는다.

## 흐름

1. 입력 수신
2. intent/entity 분석
3. session context 조회
4. 슬롯 충족 여부 판단
5. deterministic scenario 실행 또는 clarify 응답
6. `dispatch_trace`, `conversation_log` 저장

## 보완 필요 항목

- intent classifier 구현 방식 결정
- entity 추출 규칙 수준 결정
- scenario 표현 방식을 JSON, 코드, DB 중 무엇으로 둘지 결정
- 금융 도메인 본인확인 단계에서 마스킹 규칙과 로그 필드 범위 결정
