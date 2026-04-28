# AGENT.md

## 역할

이 문서는 FlowMind 저장소에서 에이전트가 따라야 할 짧은 허브 문서다.
상세 규칙은 `docs/`가 system of record이며, 이 문서는 무엇을 먼저 읽고 어떻게 작업을 시작할지 안내한다.

## AGENT.md vs AGENTS.md

- `AGENTS.md`: 사람/AI 공통 진입 문서(프로젝트 스냅샷, 공통 규칙, 기본 핸드오프 형식)
- `AGENT.md`: 현재 세션 운영 허브(하네스 트리거, 작업 흐름, 컨텍스트 우선순위)

## 하네스: FlowMind Delivery Harness

**목표:** FlowMind 작업을 오케스트레이터 기반으로 분해해 구현, 문서, 검증을 일관되게 완료한다.

**트리거:** FlowMind 구현/수정/검증/문서 동기화 요청 시 `flowmind-orchestrator` 스킬을 사용한다. 하위 에이전트 통제/게이트 운영 요청 시 `flowmind-ai-control` 스킬을 함께 사용한다. 단순 질의응답은 직접 처리 가능하다.

**변경 이력:**
| 날짜 | 변경 내용 | 대상 | 사유 |
|------|----------|------|------|
| 2026-04-27 | 초기 하네스 구성 | agents/, skills/, AGENT.md | 에이전트 팀 기반 실행 체계 도입 |
| 2026-04-27 | AI 협업 통제 체계 이식 | AGENTS.md, docs/guides, docs/operations, skills | 오케스트레이터 중심 통제 규칙 반영 |
| 2026-04-27 | 오케스트레이터 자동화 도구 추가 | scripts/, docs/operations | 이슈 기반 자동 배정과 게이트 운영 지원 |

## 절대 규칙

1. 구현, 수정, 삭제, 리팩터링, 문서 변경 전에는 반드시 계획을 작성한다.
2. 계획은 사용자에게 먼저 보여주고 승인받는다.
3. 승인 전에는 탐색, 분석, 리스크 정리, 계획 수립까지만 수행한다.
4. 승인받은 범위를 벗어나는 변경은 다시 승인받는다.
5. `AGENT.md`는 200라인 이하로 유지하고 세부 내용은 `docs/`로 분리한다.

## 기본 작업 흐름

1. 요청을 한두 문장으로 재정리한다.
2. 관련 컨텍스트 문서를 식별해 읽는다.
3. 목표, 범위, 예상 파일, 리스크, 검증 방법을 포함한 계획을 작성한다.
4. 사용자 승인 요청을 한다.
5. 승인 후에만 파일을 수정한다.
6. 검증 결과와 남은 리스크를 보고한다.

## 공통 필수 컨텍스트

- [AGENTS.md](./AGENTS.md)
- [README.md](./README.md)
- [docs/README.md](./docs/README.md)
- [docs/planning/PLANS.md](./docs/planning/PLANS.md)
- [docs/guides/ai-collaboration.md](./docs/guides/ai-collaboration.md)
- [docs/operations/orchestrator-agent-playbook.md](./docs/operations/orchestrator-agent-playbook.md)

## 현재 작업 기준점

- `Phase 1 > Project Bootstrap`은 구현 완료 상태다
- 현재 기준 구현 베이스는 `backend/` Spring Boot bootstrap이다
- 다음 우선순위는 `Phase 2 > Deterministic Core`다
- 실행 계획 문서는 active/completed 상태를 구분해서 본다

## 작업 유형별 컨텍스트

### 방향과 범위

- [docs/guides/project-overview.md](./docs/guides/project-overview.md)
- [docs/planning/phase-roadmap.md](./docs/planning/phase-roadmap.md)
- [docs/planning/PHASES.md](./docs/planning/PHASES.md)
- [docs/planning/rapeech-execution-plan.md](./docs/planning/rapeech-execution-plan.md)

### 아키텍처와 구현 원칙

- [docs/architecture/ARCHITECTURE.md](./docs/architecture/ARCHITECTURE.md)
- [docs/guides/implementation-guidelines.md](./docs/guides/implementation-guidelines.md)
- [docs/architecture/flowmind-hybrid-strategy.md](./docs/architecture/flowmind-hybrid-strategy.md)

### 대화 시스템과 도메인 모델

- [docs/guides/conversation-system-breakdown.md](./docs/guides/conversation-system-breakdown.md)
- [docs/operations/RELIABILITY.md](./docs/operations/RELIABILITY.md)
- [docs/operations/SECURITY.md](./docs/operations/SECURITY.md)

### 작업 계획과 품질 기준

- [docs/operations/QUALITY_SCORE.md](./docs/operations/QUALITY_SCORE.md)
- [docs/planning/exec-plans/active/README.md](./docs/planning/exec-plans/active/README.md)
- [docs/planning/exec-plans/completed/README.md](./docs/planning/exec-plans/completed/README.md)
- [docs/planning/templates/execution-plan-template.md](./docs/planning/templates/execution-plan-template.md)

### 협업 규칙

- [docs/guides/git-workflow.md](./docs/guides/git-workflow.md)
- [docs/guides/agent-context-planning.md](./docs/guides/agent-context-planning.md)
- [docs/guides/agent-context-conventions.md](./docs/guides/agent-context-conventions.md)

## 현재 프로젝트의 핵심 제약

- FlowMind는 하이브리드 대화 플랫폼이다.
- 초기 MVP는 백엔드 중심이다.
- Java/Spring Boot가 우선이다.
- 현재 저장소에는 Spring Boot bootstrap이 이미 반영돼 있다.
- `Intent + Scenario + LLM fallback`을 먼저 끝까지 동작시킨다.
- prompt file 기반 템플릿 관리와 `dispatch_trace` 저장을 초기에 반영한다.

## 템플릿 사용 규칙

- 기능 작업은 가능하면 이슈 템플릿부터 작성한다.
- PR 작성 시 저장소의 PR 템플릿을 기준으로 작성한다.
- 커밋 메시지는 `.gitmessage.txt` 형식을 따른다.
- phase 문서와 execution plan이 있는 작업은 이슈와 PR에 반드시 연결한다.

## 승인 전 금지 항목

- 파일 생성 및 수정
- 구조 리팩터링
- 스키마 변경
- 의존성 추가
- 문서 체계 변경
- 삭제 작업

읽기 전용 탐색과 계획 수립만 승인 전에 가능하다.
