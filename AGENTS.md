# AGENTS.md

FlowMind 저장소의 AI 작업 진입점 문서다. 상세 규칙은 연결된 문서를 따른다.

## Role Boundary

- 이 문서는 사람/AI가 공통으로 참고하는 진입 문서다.
- 세션 실행 디테일(하네스 트리거, 세부 흐름)은 `AGENT.md`를 따른다.

## Project Snapshot

- 제품: 규칙 기반 Intent 처리 + LLM fallback 하이브리드 대화 플랫폼
- 현재 단계: Phase 1 bootstrap 완료, Phase 2 Deterministic Core 진행 대상
- 백엔드: Java 21, Spring Boot 3.x, Flyway, PostgreSQL
- 운영 목표: dispatch trace, conversation log, 검증 가능한 품질 게이트

## Read Next

- `README.md`
- `docs/README.md`
- `docs/guides/ai-collaboration.md`
- `docs/operations/orchestrator-agent-playbook.md`
- `docs/operations/orchestrator-manager-automation.md`
- `docs/guides/agent-context-planning.md`
- `docs/guides/agent-context-conventions.md`
- `docs/planning/PHASES.md`
- `docs/planning/PLANS.md`

## Working Rules

- 오케스트레이터 1인 의사결정 원칙을 유지한다.
- 작업은 하나의 slice 단위로 분리한다.
- 구현/리뷰/디버깅을 한 지시에서 섞지 않는다.
- 범위 밖 파일 수정은 거부한다.
- 검증 증거 없는 완료 선언을 금지한다.

## Mandatory Handoff Format

```text
변경 파일 목록
- ...

검증 결과
- command: result

남은 리스크
- ...
```

## Do Not

- 계획 없는 대규모 변경 금지
- 하위 에이전트의 자의적 범위 확장 금지
- 보안/설정 경계 파일 무단 수정 금지
- 테스트 실패 상태에서 완료 처리 금지

<!-- SPECKIT START -->
For additional context about technologies to be used, project structure,
shell commands, and other important information, read the current plan
<!-- SPECKIT END -->
