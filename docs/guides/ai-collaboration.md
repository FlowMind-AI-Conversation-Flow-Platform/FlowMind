# AI Collaboration Guide

이 문서는 FlowMind에서 AI와 협업할 때의 통제 규칙을 정의한다.

## 운영 원칙

- 오케스트레이터만 범위/순서/완료를 확정한다.
- 작업은 독립 slice로 분리하고, slice별 책임 파일을 명시한다.
- 구현과 리뷰를 분리한다.
- 하위 에이전트는 지시된 범위를 확장하지 않는다.
- 코드, 문서, 검증은 별도 패스로 관리한다.

## 역할 모델

- Orchestrator
- 범위 고정, 우선순위 결정, 병렬/순차 실행 결정
- Worker
- 한 slice 구현만 담당, 고정 포맷으로 결과 보고
- Reviewer (Spec/Quality/QA)
- 차단 이슈 또는 no-findings만 보고

## 오케스트레이터 권한

다음 항목은 오케스트레이터만 결정한다.

- 이슈 우선순위
- 병렬 실행 여부
- 머지 순서
- done/not-done

## 서브에이전트 제어 루프

1. Contract freeze
- 목표, 파일 범위, 금지 파일, 검증 명령 고정

2. Dispatch
- 독립 slice에 worker 1명 할당
- 충돌 위험이 있으면 순차 실행

3. Collect
- 고정 핸드오프 포맷으로 결과 수집

4. Gate
- spec/quality/qa 리뷰로 차단 이슈 확인

5. Integrate
- 검증 증거 확인 후 통합

## 필수 워커 보고 포맷

```text
변경 파일 목록
- ...

검증 결과
- command: result

남은 리스크
- ...
```

## 충돌 위험 기준

아래 중 하나라도 해당하면 순차 실행:

- 같은 파일 수정
- 같은 API/컨트롤러/DTO 경계 수정
- 같은 설정 파일 수정 (`application.yml`, 보안 설정)
- 같은 스키마/마이그레이션 대상 수정

## 공유 경계 파일 정책

아래 파일은 오케스트레이터 사전 승인 없이 수정 금지:

- `backend/src/main/resources/application.yml`
- 보안/인증 관련 설정 코드
- 공용 DTO/응답 계약 타입
- 빌드/의존성 파일 (`build.gradle.kts`, `package.json`)

## 위반 처리

계약 위반 출력은 즉시 반려한다.

- 범위 밖 파일 수정
- 검증 증거 누락
- 무관 리팩터링 포함

반려 후 같은 작업자를 같은 slice로 재할당해 수정한다.

## 완료 정의 (DoD)

아래 모두 만족해야 완료다.

- slice 계약 충족
- 필수 검증 통과
- 차단 이슈 해소
- 문서 반영 완료
