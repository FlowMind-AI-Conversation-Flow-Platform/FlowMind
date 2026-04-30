# Feature Specification: FlowMind Finance VoiceOps MVP

**Feature Branch**: `001-flowmind-finance-voiceops`  
**Created**: 2026-04-30  
**Status**: Draft  
**Input**: User description: "FlowMind Finance VoiceOps MVP intents and fallback"

## User Scenarios & Testing *(mandatory)*

<!--
  IMPORTANT: User stories should be PRIORITIZED as user journeys ordered by importance.
  Each user story/journey must be INDEPENDENTLY TESTABLE - meaning if you implement just ONE of them,
  you should still have a viable MVP (Minimum Viable Product) that delivers value.
  
  Assign priorities (P1, P2, P3, etc.) to each story, where P1 is the most critical.
  Think of each story as a standalone slice of functionality that can be:
  - Developed independently
  - Tested independently
  - Deployed independently
  - Demonstrated to users independently
-->

### User Story 1 - 본인확인 후 계좌 정보 안내 (Priority: P1)

고객이 이름/생년월일/휴대폰 뒤4자리(또는 고객번호)로 본인확인을 완료하면 계좌 요약 정보를 음성/텍스트로 안내받는다.

**Why this priority**: 금융 콜플로우에서 인증 후 기본 조회는 가장 빈도가 높고 실패 시 고객 불만이 커서 최우선이다.

**Independent Test**: 인증 슬롯이 모두 채워진 요청으로 `/api/dispatch` 호출 시 `ACCOUNT_INFO_INQUIRY`로 분류되고 인증 성공 응답을 단독 검증할 수 있다.

**Acceptance Scenarios**:

1. **Given** 고객이 인증 슬롯을 모두 제공한 상태, **When** 계좌 정보를 요청하면, **Then** 인증 성공 후 계좌 요약이 반환된다.
2. **Given** 인증 슬롯 일부가 누락된 상태, **When** 계좌 정보를 요청하면, **Then** 누락 슬롯에 대한 후속 질문이 반환된다.

---

### User Story 2 - 이체/거래내역 조회 (Priority: P2)

고객이 계좌, 기간, 거래유형을 말하면 거래내역을 조회하고 핵심 결과를 응답한다.

**Why this priority**: 인증 다음 빈출 업무이며 슬롯 누락 보완 대화 품질을 보여주기 좋다.

**Independent Test**: 거래내역 관련 입력만으로 `TRANSACTION_HISTORY_INQUIRY` 분류, 슬롯 수집, 결과 응답까지 독립 검증 가능하다.

**Acceptance Scenarios**:

1. **Given** 계좌와 기간만 제공된 상태, **When** 거래내역 조회 요청이 들어오면, **Then** 거래유형 누락 질문 후 슬롯 충족 시 조회 결과를 반환한다.

---

### User Story 3 - 카드 분실/한도 긴급 처리 (Priority: P3)

고객의 카드 분실정지 또는 한도조회 요청을 정책 분기하고 필요 시 상담원 연결 fallback을 수행한다.

**Why this priority**: 긴급성은 높지만 정책 분기/에스컬레이션 설계가 추가로 필요해 P3로 둔다.

**Independent Test**: 카드 업무 입력에 대해 `CARD_LOST_OR_LIMIT` 분류 후 요청유형에 따른 정책 분기와 상담원 연결 조건을 독립 검증 가능하다.

**Acceptance Scenarios**:

1. **Given** 고객이 분실정지를 요청한 상태, **When** 인증이 완료되면, **Then** 분실정지 프로세스로 분기하고 완료/상담원 연결 응답을 반환한다.

---

[Add more user stories as needed, each with an assigned priority]

### Edge Cases

<!--
  ACTION REQUIRED: The content in this section represents placeholders.
  Fill them out with the right edge cases.
-->

- 의도 신뢰도가 임계값 미만이면 어떤 fallback reason으로 기록되는가?
- 한 문장에 계좌조회+분실정지가 함께 들어오면 복합 요청으로 처리되는가?
- LLM provider 장애(타임아웃/연결실패) 시 표준 오류 포맷이 유지되는가?
- 인증 슬롯 값이 형식 오류(예: 생년월일 자리수 불일치)일 때 재질문 정책은 무엇인가?

## Requirements *(mandatory)*

<!--
  ACTION REQUIRED: The content in this section represents placeholders.
  Fill them out with the right functional requirements.
-->

### Functional Requirements

- **FR-001**: System MUST classify user requests into `ACCOUNT_INFO_INQUIRY`, `TRANSACTION_HISTORY_INQUIRY`, `CARD_LOST_OR_LIMIT`, or `LLM_FALLBACK`.
- **FR-002**: System MUST perform slot-filling for each intent and return follow-up prompts for missing required slots.
- **FR-003**: System MUST apply identity verification before returning account-sensitive information.
- **FR-004**: System MUST route to LLM fallback when confidence is below threshold, request is composite, or complaint/sentiment escalation is detected.
- **FR-005**: System MUST return standardized response envelope including `status`, `requestId`, and timestamped errors where applicable.
- **FR-006**: System MUST persist fallback reason and dispatch trace for each request.
- **FR-007**: System MUST expose measurable metrics for misclassification rate, fallback ratio, and response latency.

### Key Entities *(include if feature involves data)*

- **DispatchRequest**: 사용자 발화와 세션 정보(`message`, `sessionId`, metadata)를 담는 입력 엔티티.
- **IntentDecision**: 분류 결과(`intent`, `confidence`, `fallbackReason`)를 담는 결정 엔티티.
- **SlotState**: 인텐트별 슬롯 채움 상태(필수/선택, 누락 목록, 수집값)를 표현하는 엔티티.
- **DispatchTrace**: 요청별 판단 근거와 처리 시간, 경로(규칙/LLM/상담원 연결)를 기록하는 엔티티.

## Success Criteria *(mandatory)*

<!--
  ACTION REQUIRED: Define measurable success criteria.
  These must be technology-agnostic and measurable.
-->

### Measurable Outcomes

- **SC-001**: P1 시나리오(본인확인 후 계좌안내) 성공률이 테스트 데이터 기준 95% 이상이다.
- **SC-002**: 슬롯 누락 후속 질문 정확도가 90% 이상이다.
- **SC-003**: LLM fallback 비율이 전체 요청의 35% 이하로 유지된다(MVP 기준).
- **SC-004**: `/api/ai/chat` 및 dispatch 핵심 응답 p95 latency가 2.0초 이하이다(로컬 실행 기준).

## Assumptions

<!--
  ACTION REQUIRED: The content in this section represents placeholders.
  Fill them out with the right assumptions based on reasonable defaults
  chosen when the feature description did not specify certain details.
-->

- MVP 범위는 한국어 금융 문의 시나리오 3종으로 제한한다.
- 음성 STT 자체 품질 개선은 범위 밖이며 텍스트 입력 정제 결과를 가정한다.
- 인증 정보는 데모/샌드박스 데이터셋으로 검증하고 실제 금융계 원장 연동은 제외한다.
- LLM fallback은 로컬/저비용 실행 구성을 우선 사용한다.
