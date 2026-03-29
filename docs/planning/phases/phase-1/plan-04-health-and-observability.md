# Phase 1 Plan 04

## 이름

Health and Observability

## 목표

프로젝트가 실행되고 있음을 검증할 수 있는 최소 health와 추후 trace/log 구조의 시작점을 만든다.

## 범위

- health endpoint
- 기본 로깅 정책
- trace/log 네이밍 초안
- 향후 `dispatch_trace`와 연결될 관찰 포인트 초안

## 완료 기준

- health endpoint가 응답한다
- 기본 로그 출력이 확인된다
- 이후 observability 구조로 연결될 최소 규칙이 정리된다

## 현재 반영 상태

- `GET /api/health` 커스텀 endpoint를 추가했다
- actuator health를 노출하고 기본 검증에 사용했다
- DB 비활성 기본 환경에서도 health가 `UP`으로 응답하도록 bootstrap 정책을 반영했다

## 리스크

- 현재 단계에서 observability를 과하게 설계할 위험
- health와 운영 trace의 경계가 모호해질 위험

## 보완 필요 항목

- 로그 포맷 정책
- trace id 도입 시점
- `dispatch_trace` 저장 지점 정의
