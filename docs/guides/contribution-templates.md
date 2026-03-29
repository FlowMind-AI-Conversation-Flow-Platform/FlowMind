# FlowMind Contribution Templates

## 목적

이 문서는 FlowMind 저장소에서 사용하는 이슈, PR, 커밋 템플릿의 목적과 사용 규칙을 설명한다.

## 위치

- 이슈 템플릿: `.github/ISSUE_TEMPLATE/`
- PR 템플릿: `.github/pull_request_template.md`
- 커밋 템플릿: `.gitmessage.txt`

## 사용 원칙

- 기능 작업은 가능하면 이슈부터 만든다
- PR에는 관련 phase 문서와 execution plan을 연결한다
- 커밋 메시지는 템플릿 형식을 따른다
- 완료된 execution plan은 `completed/`, 진행 중 계획은 `active/` 기준으로 연결한다
- 문서 PR이라도 현재 구현 상태와 계획 상태를 혼동하지 않도록 확인한다

## 이슈 템플릿 종류

- `feature.md`
- `bug.md`
- `docs.md`

## PR 템플릿 핵심 항목

- 변경 내용
- 변경 이유
- 검토 포인트
- 검증
- 관련 문서
- 관련 이슈

## 커밋 템플릿 핵심 항목

- `type: 제목`
- 변경 이유
- 주요 변경
- 영향 범위
- 검증

## 권장 연결 방식

- 이슈 -> phase 문서
- 이슈 -> execution plan
- PR -> 이슈
- PR -> phase 문서 / execution plan
- 커밋 -> 작업 목적 단위

## 현재 저장소 기준 예시

- bootstrap 관련 PR:
  - phase 문서 -> `phase-1/plan-01-project-bootstrap.md`
  - execution plan -> `exec-plans/completed/spring-boot-foundation.md`
- 다음 구현 PR:
  - phase 문서 -> `phase-2/plan.md`
  - execution plan -> 새 active plan 작성 후 연결
