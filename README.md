# FlowMind

FlowMind는 `규칙 기반 Intent 처리`와 `LLM 기반 Fallback`을 결합한 하이브리드 대화 플랫폼입니다.

이 프로젝트의 목표는 단순한 챗봇이 아니라, 실제 서비스 운영에서 다음을 동시에 만족하는 구조를 만드는 것입니다.

- 반복적이고 명확한 요청은 시나리오 기반으로 안정 처리
- 애매하거나 맥락 의존적인 요청은 LLM으로 유연 처리
- 모든 판단과 응답을 로그로 남겨 지속적으로 개선

## 핵심 개념

FlowMind는 세 가지 축으로 동작합니다.

- `Context Engineering`
  - 사용자 정보, 세션 상태, 도메인 규칙을 함께 보고 입력을 해석합니다.
- `Scenario Execution`
  - 신뢰도가 높은 요청은 규칙 기반 플로우로 처리합니다.
- `Prompt Engineering`
  - 애매하거나 복합적인 요청은 컨텍스트가 보강된 프롬프트로 LLM에 전달합니다.

즉, "모든 것을 LLM으로 처리"하지 않고, 가능한 것은 규칙 기반으로 처리하고 어려운 것만 생성형으로 넘기는 구조입니다.

## 현재 문서 구성

문서 전체 목록은 [docs/README.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/README.md)에서 볼 수 있습니다.

우선순위대로 읽으려면 아래 순서를 권장합니다.

1. [docs/project-overview.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/project-overview.md)
2. [docs/conversation-system-breakdown.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/conversation-system-breakdown.md)
3. [docs/convoforge-hybrid-strategy.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/convoforge-hybrid-strategy.md)
4. [docs/phase-roadmap.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/phase-roadmap.md)
5. [docs/implementation-guidelines.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/implementation-guidelines.md)
6. [docs/git-workflow.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/git-workflow.md)
7. [docs/reference-from-spring-rag-application.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/reference-from-spring-rag-application.md)

## MVP 범위

초기 구현은 아래 범위에 집중합니다.

- Intent 3~5개
- 대표 시나리오 1~2개
- Slot filling 가능한 대화 흐름 1개
- LLM fallback 1개
- 대화 로그 저장
- 기본 운영 지표 수집

## 개발 원칙

- 프롬프트는 코드 문자열이 아니라 관리 대상 자산으로 다룹니다.
- LLM 호출은 단일 게이트웨이로 통제합니다.
- Dispatcher 판단 과정은 trace로 남깁니다.
- 설정값은 코드 상수가 아니라 운영 가능한 설정으로 분리합니다.
- `main / dev / feat-*` 브랜치 전략으로 개발합니다.

## 다음 단계

다음 구현 단계는 아래 순서로 진행합니다.

1. 프로젝트 기본 구조 생성
2. PostgreSQL / Redis / Flyway 설정
3. Intent / Scenario / SessionContext 도메인 모델 정의
4. Dispatch API와 대표 시나리오 1개 구현
5. LLM fallback과 trace/log 구조 추가
