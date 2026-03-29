# FlowMind Phases

## 목적

이 문서는 FlowMind의 페이즈별 실행 문서를 관리하는 상위 인덱스다.

하네스 엔지니어링 관점에서 페이즈는 단순 로드맵이 아니라, 실제 작업 단위 문서 집합으로 관리한다.

## 운영 원칙

- 각 phase는 최소한 `plan.md`, `design.md`를 가진다
- 필요하면 `plan-01-*.md` 같은 세부 계획으로 분해한다
- 각 문서에는 현재 상태와 보완 필요 항목이 포함된다
- phase 문서는 실행 중 계속 갱신한다

## 현재 페이즈

### Phase 1. Backend Foundation

- 상태: `in_progress`
- 현재 메모:
  - Spring Boot bootstrap 구현 완료
  - runtime config / storage foundation / health baseline 반영 완료
  - 다음 단계는 deterministic core로 연결되는 domain / dispatch 구조 확장
- 문서:
  - [phase-1/plan.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan.md)
  - [phase-1/design.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/design.md)
  - [phase-1/plan-01-project-bootstrap.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-01-project-bootstrap.md)
  - [phase-1/plan-02-runtime-config.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-02-runtime-config.md)
  - [phase-1/plan-03-storage-foundation.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-03-storage-foundation.md)
  - [phase-1/plan-04-health-and-observability.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-04-health-and-observability.md)
  - [exec-plans/completed/spring-boot-foundation.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/exec-plans/completed/spring-boot-foundation.md)

### Phase 2. Deterministic Core

- 상태: `pending`
- 문서:
  - [phase-2/plan.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-2/plan.md)
  - [phase-2/design.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-2/design.md)

### Phase 3. Hybrid Routing

- 상태: `pending`
- 문서:
  - [phase-3/plan.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-3/plan.md)
  - [phase-3/design.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-3/design.md)

## 다음 확장

향후 필요 시 아래 phase도 같은 구조로 추가한다.

- Phase 4. Ops & Analytics
- Phase 5. Productization
