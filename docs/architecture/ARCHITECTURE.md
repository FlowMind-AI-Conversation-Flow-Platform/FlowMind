# FlowMind Architecture

## 목적

이 문서는 FlowMind의 상위 아키텍처 원칙과 핵심 경계를 정의한다.

## 시스템 한 줄 정의

FlowMind는 `Intent 기반 시나리오 실행`과 `LLM fallback`을 결합한 하이브리드 대화 플랫폼이다.

## 핵심 레이어

### 1. Context Layer

- `UserContext`
- `SessionContext`
- `DomainContext`

역할:

- 현재 입력을 단독 문장으로 보지 않고 맥락 안에서 해석한다

### 2. Decision Layer

- `ContextAwareIntentClassifier`
- `EntityExtractor`
- `SlotFiller`
- `HybridDispatcher`

역할:

- 입력을 분류하고 어떤 경로로 처리할지 결정한다

### 3. Execution Layer

- `ScenarioExecutor`
- `LlmGateway`
- `PromptAugmentation`

역할:

- 시나리오 실행 또는 LLM fallback 응답 생성

### 4. Ops Layer

- `ConversationLog`
- `DispatchTrace`
- metrics / analytics

역할:

- 판단 과정과 결과를 저장하고 개선 루프를 만든다

## MVP 아키텍처 원칙

- 백엔드 중심으로 시작한다
- Java / Spring Boot 우선
- LLM은 보조 축이다
- prompt file 기반 템플릿 관리를 먼저 사용한다
- `dispatch_trace`는 MVP부터 저장한다

## 허용되는 핵심 구성요소

- Spring Boot
- PostgreSQL
- Redis
- Flyway
- OpenAI API

## MVP에서 보류하는 것

- full RAG
- 다중 provider registry
- 복잡한 agent orchestration
- 실시간 음성 스트리밍
- 대형 프론트엔드 편집기

## 구현 경계 원칙

- LLM 호출은 `LlmGateway`로만 진입한다
- Dispatcher는 단계형 서비스로 나눈다
- 설정값은 코드 상수보다 설정 파일 또는 운영 설정으로 둔다
- prompt는 코드 문자열보다 파일 또는 템플릿 레이어에 둔다
- 로그는 결과보다 판단 과정을 남긴다
