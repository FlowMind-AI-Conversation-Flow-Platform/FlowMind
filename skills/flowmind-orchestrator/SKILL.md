---
name: flowmind-orchestrator
description: "FlowMind 저장소 작업을 에이전트 팀으로 조율한다. FlowMind 기능 구현, 백엔드 수정, 문서 동기화, 품질 검증, 하네스 재실행, 부분 재실행, 업데이트, 보완, 이전 결과 기반 개선 요청 시 반드시 이 스킬을 사용한다."
---

# FlowMind Orchestrator

FlowMind 저장소 작업을 오케스트레이션하여 구현, 문서, 검증을 일관되게 마무리한다.

## 실행 모드: 에이전트 팀

## 에이전트 구성

| 팀원 | 타입 | 역할 | 연결 스킬 | 출력 |
|------|------|------|----------|------|
| orchestrator-lead | custom | 작업 분해/통합 | flowmind-orchestrator | 최종 결과 보고 |
| backend-builder | custom | 백엔드 구현 | flowmind-backend-implementation | 코드/테스트 결과 |
| docs-keeper | custom | 문서 정합성 | flowmind-doc-sync | 문서 변경 |
| qa-reviewer | custom | 검증/회귀 점검 | flowmind-qa-gate | QA 보고 |

## 워크플로우

### Phase 0: 컨텍스트 확인
1. `_workspace/` 존재 여부를 확인한다.
2. 실행 방식을 결정한다.
- `_workspace/` 없음: 초기 실행
- `_workspace/` 있음 + 부분 수정 요청: 부분 재실행
- `_workspace/` 있음 + 새 입력: 기존 `_workspace/`를 보존하고 새 실행

### Phase 1: 준비
1. 사용자 요청을 목표/범위/비범위로 분해한다.
2. 관련 컨텍스트 문서를 읽고 현재 phase를 확인한다.
3. 필요 시 `_workspace/00_input.md`에 입력 요약을 저장한다.

### Phase 2: 작업 분배
1. backend-builder에 구현/수정 작업을 할당한다.
2. docs-keeper에 문서 반영 작업을 할당한다.
3. qa-reviewer에 검증 범위를 할당한다.
4. 각 작업에 대해 contract를 고정한다.
- 목표
- 파일 범위
- 비수정 파일
- 검증 명령
- 핸드오프 포맷

### Phase 3: 병렬 실행 및 동기화
1. 각 에이전트는 자신의 범위를 수행한다.
2. 의존 정보가 생기면 팀 통신으로 즉시 공유한다.
3. 충돌이 생기면 orchestrator-lead가 우선순위를 정한다.

### Phase 4: 통합
1. 코드 변경, 문서 변경, QA 결과를 수집한다.
2. 누락/충돌/미검증 항목을 정리한다.
3. 최종 보고를 작성한다.

## 데이터 전달 규칙
- 기본: 메시지 기반 + 파일 기반 혼합
- 코드/문서 산출물은 저장소 파일을 단일 진실 원천으로 사용
- QA 결과는 재현 명령과 함께 전달
- 핸드오프는 아래 형식을 강제한다.

```text
변경 파일 목록
- ...

검증 결과
- command: result

남은 리스크
- ...
```

## 에러 핸들링
- 단일 실패: 1회 재시도 후 부분 결과로 진행
- 과반 실패: 진행 중단 후 사용자에게 상황 보고
- 상충 데이터: 삭제하지 않고 출처 병기
- 범위 위반 출력: 즉시 반려 후 같은 담당에게 재할당

## 테스트 시나리오

### 정상 흐름
1. 사용자가 "Dispatch API 구현" 요청
2. backend-builder가 코드 구현 및 테스트 실행
3. docs-keeper가 phase/plan 상태 반영
4. qa-reviewer가 회귀 검증
5. 최종 결과 보고

### 에러 흐름
1. backend 테스트 실패
2. qa-reviewer가 실패 재현 경로 공유
3. backend-builder가 수정 후 재검증
4. 재실패 시 미해결 리스크 명시 후 부분 완료 보고
