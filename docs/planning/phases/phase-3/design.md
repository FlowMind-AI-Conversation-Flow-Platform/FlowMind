# Phase 3 Design

## 목적

Intent 기반 시나리오와 LLM fallback을 연결하는 최소 하이브리드 구조를 정의한다.

## 핵심 컴포넌트

- `HybridDispatcher`
- `PromptLoader`
- `PromptAugmentationService`
- `LlmGateway`
- `ResponseGuard`

## 라우팅 기준

### Scenario로 갈 때

- intent confidence가 충분히 높다
- required slot이 충족되었거나 deterministic clarify가 가능하다
- 업무 정책상 예측 가능한 실행이 필요하다

### LLM으로 갈 때

- intent가 애매하다
- 표현이 복합적이거나 감정/불만이 강하다
- 안전한 deterministic scenario가 없다
- 금융 정책상 deterministic 자동 처리가 허용되지 않는다

## 출력 계약

LLM 출력은 자유 텍스트만이 아니라 다음 정보를 포함하는 구조를 지향한다.

- `message`
- `action`
- `next_step`
- `reason`

## 보완 필요 항목

- prompt file naming 규칙
- masking 규칙
- llm error handling 정책
- fallback 응답에서 상담원 이관 문구/조건 표준화
