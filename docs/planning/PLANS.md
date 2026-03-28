# FlowMind Plans

## 목적

이 문서는 FlowMind에서 실행 계획을 어떻게 만들고 관리할지 정의한다.

하네스 엔지니어링 관점에서 계획은 일회성 메모가 아니라 저장소 안에 남는 운영 산출물이다.

## 원칙

- 큰 작업은 반드시 문서화된 execution plan을 가진다
- 작은 작업도 최소한 대화 내 계획과 사용자 승인을 거친다
- 진행 중 계획과 완료된 계획을 분리한다
- 계획은 코드와 함께 진화한다

## 디렉터리 구조

- `docs/exec-plans/active/`
  - 현재 진행 중이거나 다음으로 진행할 계획
- `docs/exec-plans/completed/`
  - 완료된 계획
- `docs/planning/templates/execution-plan-template.md`
  - 새 계획 문서 작성 템플릿

## 언제 plan 문서를 만들까

아래 중 하나에 해당하면 execution plan 문서를 만든다.

- 여러 파일을 건드리는 기능 작업
- 아키텍처 변경
- 스키마 또는 데이터 모델 변경
- 단계별 검증이 필요한 작업
- 사용자 승인 후 여러 세션에 걸쳐 진행될 작업

## plan 문서 최소 항목

- 목표
- 범위
- 비범위
- 예상 변경 파일
- 리스크
- 단계별 작업
- 검증 기준
- 진행 상태

## 운영 규칙

- 새 작업은 먼저 `active`에 만든다
- 완료되면 `completed`로 이동한다
- 계획 없이 큰 변경을 시작하지 않는다
- 계획 내용이 바뀌면 문서도 같이 갱신한다

## 현재 권장 첫 계획

FlowMind는 아래 계획부터 execution plan으로 관리하는 것이 좋다.

- Spring Boot 백엔드 초기화
- dispatch API 및 session context 구현
- billing inquiry 시나리오 구현
- LLM fallback 및 trace 저장 구현
