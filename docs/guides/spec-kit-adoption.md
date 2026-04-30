# Spec-Kit Minimal Adoption (FlowMind)

## 목표

- 기존 `FlowMind` 문서/오케스트레이션 체계를 유지하면서 spec-kit을 최소 비용으로 도입한다.
- 스펙 중심 산출물(요구사항/계획/작업 목록)을 `docs/planning` 운영 흐름과 연결한다.

## 적용 범위 (MVP)

1. spec-kit CLI 설치 및 초기화
2. 스펙 산출물 생성 경로 표준화
3. 오케스트레이터 strict-gate와 문서 품질 검증 연계

## 1) 설치

```powershell
uv tool install specify-cli --from git+https://github.com/github/spec-kit.git
specify version
```

## 2) 프로젝트 초기화

루트(`FlowMind`)에서 1회 실행:

```powershell
specify init --here --integration codex
```

초기화 후 생성되는 spec-kit 기본 파일은 유지하고, 아래 매핑 기준으로 운영한다.

## 3) FlowMind 매핑 규칙

- spec-kit 산출물 원본: `.specify/` (spec-kit 기본 경로)
- FlowMind 운영 문서: `docs/planning/`
- 동기화 원칙:
  - 스펙 초안은 `.specify/`에서 작성
  - 승인/운영 기준본은 `docs/planning/exec-plans/active/`로 반영
  - 완료 시 `docs/planning/exec-plans/completed/`로 이동

## 4) 권장 실행 순서

1. 요구사항 정리: `/speckit.specify`
2. 구현 계획 생성: `/speckit.plan`
3. 작업 분해: `/speckit.tasks`
4. 구현 실행: `/speckit.implement`
5. FlowMind 오케스트레이터 실행:

```powershell
node scripts/orchestrator-manager.mjs run --execute --create-worktrees --comment --strict-gate
```

## 5) 운영 규칙

- PR 본문은 우선 `.codex/orchestrator/pr-bodies/issue-<n>.md`를 사용한다.
- spec 산출물을 planning 실행계획으로 동기화할 때:

```powershell
# 미리보기
node scripts/sync-spec-to-planning.mjs --feature 001-flowmind-finance-voiceops --dry-run

# 파일 생성
node scripts/sync-spec-to-planning.mjs --feature 001-flowmind-finance-voiceops

# 전체 spec 일괄 동기화
node scripts/sync-spec-to-planning.mjs --all --dry-run
node scripts/sync-spec-to-planning.mjs --all

# 수동 노트 보존 모드(리스크/진행 상태/작업 단계 유지)
node scripts/sync-spec-to-planning.mjs --feature 001-flowmind-finance-voiceops --mode append-notes
```

- 생성 대상: `docs/planning/exec-plans/active/<feature>.md`
- 실행 후 요약 리포트가 출력된다:
  - `total`, `created`, `updated`, `skipped`
  - 처리 파일 목록
- 모드:
  - `overwrite` (기본): 전체 재생성
  - `append-notes`: 기존 문서의 수동 구간(`리스크`, `작업 단계`, `진행 상태`) 보존
- 추출 기준:
  - 목표: `FR-*` 요구사항 라인 우선
  - 범위: `Given/When/Then` 수용 시나리오 우선
  - 검증 기준: `SC-*` 성공지표 라인 우선
- 자동 생성이 없는 경우:

```powershell
node scripts/create-pr-from-issue.mjs --base dev

# spec -> planning 동기화까지 한 번에 수행
node scripts/create-pr-from-issue.mjs --base dev --sync-feature 001-flowmind-finance-voiceops
```

- docs/planning 문서 변경 시 strict-gate lint(링크/상태 일관성)를 통과해야 한다.

## 6) 성공 기준

- 신규 기능 이슈 1건을 spec-kit 경유로 기획하고 PR까지 완료
- `docs/planning` 상태 표기와 링크 무결성 검증을 통과
- 핸드오프 3종(변경파일/검증/리스크)이 PR 본문에 포함
