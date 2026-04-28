# Orchestrator Agent Playbook

FlowMind에서 오케스트레이터가 하위 에이전트를 통제하는 실행 플레이북.

## 목적

- 단일 의사결정자로 범위와 순서를 통제한다.
- Worker를 좁고 결정적인 단위로 유지한다.
- 구현/리뷰/디버깅 혼합 작업을 방지한다.

## Dispatch Rules

1. 한 번에 한 slice만 지시한다.
2. 한 지시에서 구현+리뷰+디버깅을 섞지 않는다.
3. 충돌 위험이 있으면 반드시 순차 실행한다.
4. 검증 명령을 구현 전에 고정한다.
5. 범위 밖 수정은 즉시 반려한다.

## Priority Matrix

- P0: 보안 결함, 인증 우회, 데이터 손상 위험
- P1: 핵심 대화 흐름 장애, release blocking bug
- P2: 비차단 기능 개선, UX 개선
- P3: 리팩터링, 문서 보완

동률 시 우선순위:

1. 사용자 영향도 높은 이슈
2. 충돌 위험 낮은 이슈
3. 구현/검증 시간이 짧은 이슈

## Mandatory Verification Matrix

- Backend slice
- `.\gradlew.bat test`
- 영향 API 계약 검증 (상태코드/필드)
- Docs slice
- 링크 유효성, phase 상태 일관성 확인
- Config/Security slice
- `.\gradlew.bat test`
- 보안 설정 영향 범위 점검

필수 계약 문구 예시:

```text
Verification:
- test must pass
- affected API contract must remain valid
- out-of-scope files must remain untouched
```

## Scope Violation Examples

- 지시하지 않은 디렉터리 수정
- 의존성 파일 무단 변경
- 검증 결과 미제출

## Violation Handling

1. 출력 즉시 반려
2. 부분 채택 금지
3. 동일 작업자에게 위반 사유와 함께 재할당
4. 재검증 증거 확인 후 통합

## Reviewer Conflict Resolution

- 보안/품질 차단 이슈가 spec-pass보다 우선
- 충돌 시 머지 차단
- 해결 근거를 exec-plan 또는 작업 로그에 기록

## Definition of Done

- acceptance contract 충족
- 필수 검증 통과
- 차단 이슈 0건
- 문서/계획 상태 최신화
