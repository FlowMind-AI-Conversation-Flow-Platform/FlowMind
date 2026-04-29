# Orchestrator Manager Automation

`scripts/orchestrator-manager.mjs`는 FlowMind 이슈 기반 작업 분배를 자동화한다.

## 기능

- `gh issue list` 기반 이슈 수집
- priority/domain 자동 분류
- 에이전트 자동 매핑
- 충돌 위험 기반 배치 분할(병렬/순차)
- 선택적 worktree/branch 생성
- 선택적 이슈 코멘트 배정
- 상태 파일 저장 및 재시도 제어
- strict gate(범위 + 검증) 실행
- docs/planning 변경 시 링크/phase 상태 lint
- PR 본문 초안 자동 생성
- 실행 이력 날짜별 아카이브 저장

## 명령

```powershell
# script self-check
node scripts/orchestrator-manager.spec.mjs

# plan only
node scripts/orchestrator-manager.mjs plan

# plan + 파일 출력
node scripts/orchestrator-manager.mjs plan --output docs/operations/orchestrator-dispatch-plan.md

# run preview (execute 없음)
node scripts/orchestrator-manager.mjs run

# execute
node scripts/orchestrator-manager.mjs run --execute --create-worktrees --comment --strict-gate
```

## 주요 옵션

- `--state <open|closed|all>`
- `--limit <n>`
- `--base <branch>`
- `--worktree-root <path>`
- `--output <path>`
- `--execute`
- `--create-worktrees`
- `--comment`
- `--strict-gate`
- `--state-file <path>`
- `--max-retries <n>`
- `--worker-command "<template>"`

## 상태 파일

- 기본 경로: `.codex/orchestrator/state.json`
- 상태값: `planned`, `running`, `dispatched`, `failed`, `done`
- 기본 리포트: `docs/operations/orchestrator-run-state.md`
- 실행 이력 아카이브: `docs/operations/orchestrator-runs/YYYY-MM-DD/*.md`
- PR 본문 초안: `.codex/orchestrator/pr-bodies/issue-<번호>.md`

## strict gate 정책

1. 범위 검사
- domain 허용 경로 밖 파일 수정 시 실패

2. 검증 검사
- backend/security/quality domain은 `.\gradlew.bat test` 필수

3. docs/planning lint
- 변경된 `docs/*.md` 링크 무결성 검사
- `docs/planning/*.md`의 `상태: \`...\`` 값 검사
- `docs/planning/PHASES.md`의 phase 상태 형식/값 검사
- 허용 상태값: `pending`, `in_progress`, `completed`
- `docs/operations/orchestrator-manager-automation.md`의 상태 카탈로그(`planned`, `running`, `dispatched`, `failed`, `done`) 일관성 검사

## PR 템플릿 자동 연동

- run 실행 시 이슈별 PR body 초안 파일을 자동 생성한다.
- 파일에는 핸드오프 3종이 자동 주입된다.
  - 변경 파일 목록
  - 검증 결과
  - 남은 리스크

사용 예시:

```powershell
gh pr create --title "feat: ..." --body-file .codex/orchestrator/pr-bodies/issue-123.md

# 자동 연동 스크립트(브랜치 feat/<issue>-... 에서 issue 추론)
node scripts/create-pr-from-issue.mjs --base dev
```

## 전제 조건

- `gh` CLI 인증 완료
- `git` 및 `node` 사용 가능
- 작업 브랜치 기준 저장소 정합성 확보
