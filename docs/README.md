# FlowMind Docs Index

FlowMind의 `docs/`는 하네스 엔지니어링 관점에서 repository knowledge의 system of record다.

## 현재 기준 구현 상태

- `Phase 1 > Project Bootstrap` 구현 완료
- `backend/` 단일 모듈 Spring Boot 프로젝트 추가
- `GET /api/health`, `GET /actuator/health` 확인 완료
- 다음 초점은 `Phase 2 > Deterministic Core`

## 폴더 구조

- `architecture/`
  - 시스템 상위 구조와 전략 문서
- `guides/`
  - 설명서, 구현 가이드, 협업 규칙
- `operations/`
  - 품질, 신뢰성, 보안 같은 운영 기준
- `planning/`
  - 로드맵, execution plan, 채용 대응 계획
- `references/`
  - 외부 참고 기반 정리 문서

## 1. 먼저 읽을 상위 문서

- [architecture/ARCHITECTURE.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/architecture/ARCHITECTURE.md)
  - 상위 아키텍처와 MVP 경계

- [planning/PLANS.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/PLANS.md)
  - execution plan 운영 규칙

- [operations/RELIABILITY.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/operations/RELIABILITY.md)
  - 로그, trace, 지표, 검증 기준

- [operations/SECURITY.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/operations/SECURITY.md)
  - PII, masking, 외부 API 연동 최소 원칙

- [operations/QUALITY_SCORE.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/operations/QUALITY_SCORE.md)
  - 현재 상태와 가장 큰 갭

## 2. 실행 계획과 템플릿

- [planning/PHASES.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/PHASES.md)
  - phase별 plan/design 인덱스

- [planning/exec-plans/active/README.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/exec-plans/active/README.md)
  - 진행 중 계획 운영 규칙

- [planning/exec-plans/completed/README.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/exec-plans/completed/README.md)
  - 완료 계획 보관 규칙

- [planning/exec-plans/completed/spring-boot-foundation.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/exec-plans/completed/spring-boot-foundation.md)
  - 완료된 Spring Boot bootstrap 실행 계획

- [planning/exec-plans/active/finance-voiceops-mvp.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/exec-plans/active/finance-voiceops-mvp.md)
  - 금융 본인확인·거래안내 하이브리드 MVP 실행 계획

- [planning/templates/execution-plan-template.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/templates/execution-plan-template.md)
  - 새 execution plan 템플릿

- [planning/phases/phase-1/plan.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan.md)
  - phase 1 실행 계획과 현재 완료 범위

- [planning/phases/phase-1/plan-01-project-bootstrap.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-01-project-bootstrap.md)
  - phase 1 세부 계획 01

- [planning/phases/phase-1/design-bootstrap.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/design-bootstrap.md)
  - phase 1 bootstrap 설계 선택지

- [planning/phases/phase-1/plan-02-runtime-config.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-02-runtime-config.md)
  - phase 1 세부 계획 02

- [planning/phases/phase-1/plan-03-storage-foundation.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-03-storage-foundation.md)
  - phase 1 세부 계획 03

- [planning/phases/phase-1/plan-04-health-and-observability.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-04-health-and-observability.md)
  - phase 1 세부 계획 04

- [planning/phases/phase-2/plan.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-2/plan.md)
  - 다음 구현 목표인 deterministic core 실행 계획

- [planning/phases/phase-3/plan.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-3/plan.md)
  - phase 3 실행 계획

## 3. 프로젝트 이해

- [guides/project-overview.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/guides/project-overview.md)
  - 프로젝트 목적, 기능, MVP

- [guides/conversation-system-breakdown.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/guides/conversation-system-breakdown.md)
  - Intent, NLU, Entity, Slot, Scenario, Dispatcher

## 4. 전략과 설계

- [architecture/flowmind-hybrid-strategy.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/architecture/flowmind-hybrid-strategy.md)
  - 하이브리드 라우팅 전략

- [references/reference-from-spring-rag-application.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/references/reference-from-spring-rag-application.md)
  - 실무형 시스템에서 참고할 구조

## 5. 실행 기준과 범위

- [planning/phase-roadmap.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phase-roadmap.md)
  - 페이즈 로드맵

- [guides/implementation-guidelines.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/guides/implementation-guidelines.md)
  - 구현 원칙과 보류 항목

- [planning/rapeech-job-fit-checklist.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/rapeech-job-fit-checklist.md)
  - 라피치 공고 기준 적합도

- [planning/rapeech-execution-plan.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/rapeech-execution-plan.md)
  - 라피치 대응 실행 계획

## 6. 에이전트와 협업

- [guides/agent-context-planning.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/guides/agent-context-planning.md)
  - 계획, 승인, 재승인 기준

- [guides/agent-context-conventions.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/guides/agent-context-conventions.md)
  - 프로젝트 컨벤션

- [guides/ai-collaboration.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/guides/ai-collaboration.md)
  - 오케스트레이터 중심 AI 협업 통제 규칙

- [guides/git-workflow.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/guides/git-workflow.md)
  - 브랜치 및 협업 규칙

- [guides/contribution-templates.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/guides/contribution-templates.md)
  - 이슈, PR, 커밋 템플릿 사용 규칙

- [operations/orchestrator-agent-playbook.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/operations/orchestrator-agent-playbook.md)
  - slice 단위 dispatch/검증/위반 처리 플레이북

- [operations/orchestrator-manager-automation.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/operations/orchestrator-manager-automation.md)
  - 이슈 기반 오케스트레이터 자동 배정/게이트 운영

## 7. 포트폴리오 자료

- [portfolio/README.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/portfolio/README.md)
  - 포트폴리오 전용 문서 인덱스

- [portfolio/project-summary.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/portfolio/project-summary.md)
  - 프로젝트 요약과 문제 정의

- [portfolio/tech-decisions.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/portfolio/tech-decisions.md)
  - 기술 선택 이유와 트레이드오프

- [portfolio/implementation-evidence.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/portfolio/implementation-evidence.md)
  - 나중에 반드시 남겨야 할 구현 증빙

- [portfolio/interview-points.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/portfolio/interview-points.md)
  - 면접 설명 포인트

- [portfolio/resume-bullets.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/portfolio/resume-bullets.md)
  - 이력서용 bullet 초안

- [portfolio/portfolio-checklist.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/portfolio/portfolio-checklist.md)
  - 포트폴리오 최종 점검표
