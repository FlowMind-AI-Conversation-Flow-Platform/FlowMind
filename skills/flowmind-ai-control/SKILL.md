---
name: flowmind-ai-control
description: "FlowMind에서 AI 작업 통제를 수행한다. 하위 에이전트 dispatch, slice 계약 고정, 범위 위반 반려, 검증 게이트 적용, done 판단 요청이 있으면 반드시 이 스킬을 사용한다."
---

# FlowMind AI Control

## 목적
오케스트레이터가 하위 에이전트 작업을 통제 가능한 방식으로 운영하도록 강제한다.

## 자동화 도구
- 이슈 기반 자동 분배가 필요하면 `scripts/orchestrator-manager.mjs`를 사용한다.
- 실행 전후로 아래 문서를 갱신한다.
- `docs/operations/orchestrator-run-state.md`
- 필요 시 `docs/operations/orchestrator-dispatch-plan.md`

## Control Loop
1. slice 계약 고정
2. 작업 분배
3. 증거 수집
4. 게이트 판정
5. 통합/반려

## slice 계약 필수 항목
- Goal 1개
- 파일 범위
- 비수정 파일
- 검증 명령
- 핸드오프 형식

## 범위 위반 처리
- 범위 밖 파일 수정: 즉시 반려
- 검증 누락: 즉시 반려
- 무관 리팩터링: 즉시 반려

반려 시 부분 채택하지 않고 동일 담당자에게 재할당한다.

## 병렬 실행 기준
- 충돌 위험이 없을 때만 병렬 실행
- 다음은 충돌 위험으로 간주한다:
- 같은 파일
- 같은 API/DTO 경계
- 같은 설정/보안 파일

## 완료 판정
아래 모두 만족해야 done:
- 계약 충족
- 검증 통과
- 차단 이슈 없음
- 문서 반영 완료
