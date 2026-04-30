# FlowMind Finance VoiceOps MVP Execution Plan

- source-spec: specs/001-flowmind-finance-voiceops/spec.md
- generated-at: 2026-04-30T09:25:41.895Z
## 제목
- FlowMind Finance VoiceOps MVP

## 목표
- **FR-001**: System MUST classify user requests into `ACCOUNT_INFO_INQUIRY`, `TRANSACTION_HISTORY_INQUIRY`, `CARD_LOST_OR_LIMIT`, or `LLM_FALLBACK`.
- **FR-002**: System MUST perform slot-filling for each intent and return follow-up prompts for missing required slots.
- **FR-003**: System MUST apply identity verification before returning account-sensitive information.
- **FR-004**: System MUST route to LLM fallback when confidence is below threshold, request is composite, or complaint/sentiment escalation is detected.
- **FR-005**: System MUST return standardized response envelope including `status`, `requestId`, and timestamped errors where applicable.
- **FR-006**: System MUST persist fallback reason and dispatch trace for each request.

## 범위
1. **Given** 고객이 인증 슬롯을 모두 제공한 상태, **When** 계좌 정보를 요청하면, **Then** 인증 성공 후 계좌 요약이 반환된다.
2. **Given** 인증 슬롯 일부가 누락된 상태, **When** 계좌 정보를 요청하면, **Then** 누락 슬롯에 대한 후속 질문이 반환된다.
1. **Given** 계좌와 기간만 제공된 상태, **When** 거래내역 조회 요청이 들어오면, **Then** 거래유형 누락 질문 후 슬롯 충족 시 조회 결과를 반환한다.
1. **Given** 고객이 분실정지를 요청한 상태, **When** 인증이 완료되면, **Then** 분실정지 프로세스로 분기하고 완료/상담원 연결 응답을 반환한다.

## 비범위
- 외부 연계 시스템 실제 운영 전환
- 인프라/배포 파이프라인 전체 재설계

## 예상 변경 파일
- backend/** (필요 기능 구현 시)
- docs/planning/**
- specs/**

## 리스크
- 사용자 수동메모: 운영 승인 필요

## 작업 단계
1. spec 요구사항 매핑
2. API/도메인 설계
3. 구현
4. 테스트/검증
5. 문서 및 운영 반영

## 검증 기준
- **SC-001**: P1 시나리오(본인확인 후 계좌안내) 성공률이 테스트 데이터 기준 95% 이상이다.
- **SC-002**: 슬롯 누락 후속 질문 정확도가 90% 이상이다.
- **SC-003**: LLM fallback 비율이 전체 요청의 35% 이하로 유지된다(MVP 기준).
- **SC-004**: `/api/ai/chat` 및 dispatch 핵심 응답 p95 latency가 2.0초 이하이다(로컬 실행 기준).

## 가정
- MVP 범위는 한국어 금융 문의 시나리오 3종으로 제한한다.
- 음성 STT 자체 품질 개선은 범위 밖이며 텍스트 입력 정제 결과를 가정한다.
- 인증 정보는 데모/샌드박스 데이터셋으로 검증하고 실제 금융계 원장 연동은 제외한다.
- LLM fallback은 로컬/저비용 실행 구성을 우선 사용한다.

## 진행 상태
- `in_progress`
