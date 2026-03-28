# Agent Context: Project Conventions

## 목적

이 문서는 FlowMind 프로젝트에서 에이전트가 따라야 할 핵심 컨벤션을 모아 둔 참조 문서다.

## 문서 컨벤션

- 프로젝트명은 항상 `FlowMind`로 통일한다
- 예전 이름이나 임시 명칭은 새 문서에서 사용하지 않는다
- MVP와 장기 목표를 구분해서 쓴다
- 현재 구현된 것과 계획 중인 것을 혼동하지 않는다

## 아키텍처 컨벤션

- 초기 MVP는 백엔드 중심이다
- `Intent + Scenario + LLM fallback`이 핵심 구조다
- LLM은 보조 축이며 규칙 기반 실행이 우선이다
- prompt file 기반 템플릿 관리를 먼저 도입한다
- `dispatch_trace`와 `conversation_log`를 초기부터 고려한다

## 구현 컨벤션

- Java / Spring Boot 우선
- PostgreSQL / Redis / Flyway 사용
- LLM 호출은 `LlmGateway` 단일 진입점으로 제한
- Dispatcher는 단계형 서비스로 분리
- 설정값은 코드 상수보다 운영 설정으로 분리

## 협업 컨벤션

- 브랜치 전략은 `main / dev / feat-*`
- 큰 작업은 계획과 승인 후 진행
- 문서와 구현이 다르면 즉시 기록하고 정리

## 포트폴리오 컨벤션

- 설명 가능한 기능부터 구현
- 데모 가능한 흐름을 우선 완성
- 라피치 공고와 직접 연결되는 산출물을 우선 만든다

## 우선 검토 문서

- [project-overview.md](./project-overview.md)
- [phase-roadmap.md](./phase-roadmap.md)
- [implementation-guidelines.md](./implementation-guidelines.md)
- [rapeech-job-fit-checklist.md](./rapeech-job-fit-checklist.md)
- [rapeech-execution-plan.md](./rapeech-execution-plan.md)
