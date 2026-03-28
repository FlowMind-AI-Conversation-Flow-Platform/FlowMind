# Phase 1 Design

## 목적

Phase 1에서 필요한 최소 백엔드 구조를 설계한다.

## 권장 구조

- `backend/src/main/java/.../config`
- `backend/src/main/java/.../common`
- `backend/src/main/java/.../health`
- `backend/src/main/resources/application.yml`
- `backend/src/main/resources/db/migration/`

## 초기 구성요소

- `FlowMindApplication`
- `HealthController`
- 설정 클래스
- 공통 예외 구조
- Flyway migration baseline

## 설계 원칙

- 기능보다 골격을 먼저 만든다
- 나중에 dispatcher 중심 구조로 확장 가능해야 한다
- 설정은 코드 상수보다 파일 기반으로 둔다

## 보완 필요 항목

- 패키지 계층 깊이 조정
- 공통 응답 포맷 도입 여부 결정
- Redis를 1단계에서 실제 연결할지 여부 확정
