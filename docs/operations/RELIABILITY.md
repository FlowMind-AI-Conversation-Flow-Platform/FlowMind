# FlowMind Reliability

## 목적

이 문서는 FlowMind의 신뢰성과 운영 가능성을 위한 최소 기준을 정리한다.

## 핵심 원칙

- 결정은 추적 가능해야 한다
- fallback은 이유와 함께 기록되어야 한다
- 검증은 가능한 한 자동화한다
- 품질 문제는 코드뿐 아니라 prompt, routing, data에서도 찾는다

## 반드시 남겨야 할 데이터

- route
- top intent 후보
- confidence
- fallback reason
- prompt version
- latency
- model name
- session summary

## 핵심 저장 대상

- `conversation_log`
- `dispatch_trace`

## MVP 지표

- scenario routing ratio
- llm fallback ratio
- slot completion rate
- average latency
- unmatched utterance count

## 검증 원칙

- 새 기능에는 직접적인 성공 경로 검증이 있어야 한다
- 시나리오 기능에는 누락 슬롯 경로 검증이 있어야 한다
- LLM fallback에는 route reason 검증이 있어야 한다
- 저장 로직에는 log/trace 생성 검증이 있어야 한다

## 운영 중 확인할 질문

- 왜 scenario로 가지 않았는가
- 왜 fallback이 과도하게 발생하는가
- 어떤 intent가 가장 자주 틀리는가
- 어떤 prompt version이 성능을 떨어뜨렸는가
