# Spring Boot Foundation Execution Plan

## 목표

FlowMind의 첫 구현 단계로 Spring Boot 백엔드 기본 골격을 만든다.

## 범위

- Spring Boot 프로젝트 초기화
- 기본 패키지 구조 생성
- health endpoint 추가
- PostgreSQL / Flyway 연결 골격 준비
- Redis는 자리만 열고 실제 연결 여부는 선택으로 둔다
- 초기 설정 파일 구성
- 최소 실행/검증 문서 정리

## 비범위

- 실제 business domain 구현
- dispatch API 구현
- scenario executor 구현
- OpenAI 연동
- 프론트엔드 구현
- security / websocket / observability 고도화

## 예상 변경 파일

- `backend/` 하위 신규 파일들
- 루트 문서 일부
- 필요 시 개발 실행 가이드

## 구현 선택 기준

- 구조: `backend/` 하위 단일 모듈
- 빌드 도구: `Gradle`
- Java: `21`
- Spring Boot: `3.x`
- 기본 패키지: `com.flowmind`

## 예상 산출물

- `backend/build.gradle.kts`
- `backend/settings.gradle.kts`
- `backend/src/main/java/com/flowmind/FlowMindApplication.java`
- `backend/src/main/java/com/flowmind/health/HealthController.java`
- `backend/src/main/resources/application.yml`
- `backend/src/main/resources/db/migration/`
- 필요 시 `backend/README.md`

## 리스크

- 초기 패키지 구조가 이후 아키텍처와 어긋날 수 있음
- DB/Redis 설정 범위를 과하게 잡으면 MVP 속도가 느려질 수 있음
- 루트와 backend build 체계를 동시에 설계하려 하면 bootstrap 목적이 흐려질 수 있음

## 작업 단계

1. `backend/` 단일 모듈 구조 확정
2. Gradle 설정과 Spring Boot 기본 의존성 구성
3. `FlowMindApplication` 및 기본 패키지 구조 생성
4. health endpoint 또는 actuator health 노출
5. `application.yml` 및 migration 경로 구성
6. 로컬 실행 방법과 최소 검증 기준 문서화

## 검증 기준

- 애플리케이션이 실행된다
- `GET /actuator/health` 또는 동등한 health endpoint가 응답한다
- `backend` 단위 build 또는 test task가 최소 1회 성공한다
- 설정 구조가 이후 dispatch API 작업으로 자연스럽게 이어진다

## 선행 참고 문서

- [plan-01-project-bootstrap.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-01-project-bootstrap.md)
- [design-bootstrap.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/design-bootstrap.md)
- [plan-02-runtime-config.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-02-runtime-config.md)
- [plan-03-storage-foundation.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-03-storage-foundation.md)

## 진행 상태

- `pending`
