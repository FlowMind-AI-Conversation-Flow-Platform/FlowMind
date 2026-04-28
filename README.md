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

## 현재 상태

- 현재 저장소는 문서 중심 설계 단계를 지나 `Phase 1`의 첫 구현 결과가 반영된 상태입니다.
- `AGENT.md`는 짧은 허브 문서이며, 실제 system of record는 `docs/`입니다.
- 큰 작업은 `execution plan`과 `phase 문서`로 관리합니다.
- `backend/` 하위에 Spring Boot 기반 백엔드 bootstrap이 추가됐고 기본 health 검증까지 끝났습니다.
- 현재 기준으로 `Project Bootstrap`은 완료됐고, 다음 구현 초점은 `Phase 2 > Deterministic Core`입니다.

## 문서 구조

문서 전체 목록은 [docs/README.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/README.md)에서 볼 수 있습니다.

현재 `docs/`는 아래처럼 나뉩니다.

- `architecture/`
  - 상위 구조와 하이브리드 전략
- `guides/`
  - 설명서, 구현 가이드, 협업 규칙
- `operations/`
  - 신뢰성, 보안, 품질 기준
- `planning/`
  - 로드맵, phase 문서, execution plans
- `references/`
  - 참고 프로젝트에서 추린 구조적 인사이트

## 추천 읽기 순서

우선순위대로 읽으려면 아래 순서를 권장합니다.

1. [docs/architecture/ARCHITECTURE.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/architecture/ARCHITECTURE.md)
2. [docs/planning/PLANS.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/PLANS.md)
3. [docs/planning/PHASES.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/PHASES.md)
4. [docs/guides/project-overview.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/guides/project-overview.md)
5. [docs/guides/conversation-system-breakdown.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/guides/conversation-system-breakdown.md)
6. [docs/architecture/flowmind-hybrid-strategy.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/architecture/flowmind-hybrid-strategy.md)
7. [docs/operations/RELIABILITY.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/operations/RELIABILITY.md)
8. [docs/planning/rapeech-execution-plan.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/rapeech-execution-plan.md)
9. [docs/references/reference-from-spring-rag-application.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/references/reference-from-spring-rag-application.md)

## 지금 바로 보면 좋은 문서

- [AGENTS.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/AGENTS.md)
- [AGENT.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/AGENT.md)
- [docs/guides/ai-collaboration.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/guides/ai-collaboration.md)
- [docs/operations/orchestrator-agent-playbook.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/operations/orchestrator-agent-playbook.md)
- [docs/operations/orchestrator-manager-automation.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/operations/orchestrator-manager-automation.md)
- [docs/planning/PHASES.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/PHASES.md)
- [docs/planning/phases/phase-1/plan-01-project-bootstrap.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-01-project-bootstrap.md)
- [backend/README.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/backend/README.md)
- [docs/planning/exec-plans/completed/spring-boot-foundation.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/exec-plans/completed/spring-boot-foundation.md)

## MVP 범위

초기 구현은 아래 범위에 집중합니다.

- Intent 3~5개
- 대표 시나리오 1~2개
- Slot filling 가능한 대화 흐름 1개
- LLM fallback 1개
- prompt template 또는 prompt file 기반 관리
- 대화 로그 저장
- 기본 운영 지표 수집

## 계획 기반 작업 방식

- 모든 실작업 전에는 계획을 먼저 작성합니다.
- 큰 작업은 `docs/planning/exec-plans/active/` 아래 execution plan으로 남깁니다.
- phase 단위 작업은 `docs/planning/phases/` 아래에서 관리합니다.
- 사용자 승인 전에는 탐색과 계획 수립까지만 진행합니다.

## 현재 착수 기준

현재 기준에서 bootstrap 구현 결과와 다음 작업 연결은 아래 문서를 기준으로 이어집니다.

1. [docs/planning/phases/phase-1/plan-01-project-bootstrap.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-01-project-bootstrap.md)
2. [docs/planning/phases/phase-1/design-bootstrap.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/design-bootstrap.md)
3. [docs/planning/exec-plans/completed/spring-boot-foundation.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/exec-plans/completed/spring-boot-foundation.md)
4. [backend/README.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/backend/README.md)

이 세 문서가 맞물려 `Phase 기준`, `설계 선택`, `실행 계획`을 분리합니다.

## 개발 원칙

- 프롬프트는 코드 문자열이 아니라 관리 대상 자산으로 다룹니다.
- LLM 호출은 단일 게이트웨이로 통제합니다.
- Dispatcher 판단 과정은 trace로 남깁니다.
- 설정값은 코드 상수가 아니라 운영 가능한 설정으로 분리합니다.
- `main / dev / feat-*` 브랜치 전략으로 개발합니다.

## 다음 단계

다음 구현 단계는 아래 순서로 진행합니다.

1. Phase 2에서 Dispatch API와 첫 deterministic 시나리오 구현
2. Intent / Entity / Slot 모델과 session context 구조 추가
3. dispatch trace와 conversation log 저장 구조 확장
4. Phase 3에서 LLM fallback과 prompt augmentation 구조 추가
