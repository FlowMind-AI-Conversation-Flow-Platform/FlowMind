# FlowMind Interview Points

## 1. 프로젝트를 왜 시작했는가

- 규칙 기반 챗봇과 LLM 단독 구조의 한계를 동시에 느꼈다
- 실무형 고객 응대 시스템은 통제와 유연성을 같이 가져야 한다고 판단했다

## 2. 핵심 차별점은 무엇인가

- Intent 기반 deterministic scenario와 LLM fallback을 분리했다
- 모든 응답보다 routing 판단과 trace를 더 중요하게 봤다
- prompt와 context를 운영 자산으로 다루도록 설계했다

## 3. 기술적으로 어려운 점은 무엇인가

- intent 분류와 scenario 실행의 경계를 설계하는 것
- low-confidence 상황에서 언제 fallback할지 기준을 잡는 것
- trace/log 구조를 미리 잡아 나중에 운영 개선이 가능하게 하는 것

## 4. 왜 좋은 포트폴리오인가

- Java 백엔드 구조를 설명할 수 있다
- 대화 모델링 능력을 보여줄 수 있다
- LLM을 단순 호출이 아니라 시스템 구성 요소로 다룬다는 점을 설명할 수 있다

## 5. 예상 질문

- 왜 모든 요청을 LLM으로 처리하지 않았나
- 왜 Spring Boot를 선택했나
- fallback 기준은 어떻게 정했나
- prompt와 route 품질은 어떻게 개선할 것인가
- trace를 남기는 이유는 무엇인가

## 6. 답변 포인트

- 비용, 일관성, 통제 가능성
- deterministic flow와 generative flow의 역할 분리
- 운영 데이터 기반 개선 루프
