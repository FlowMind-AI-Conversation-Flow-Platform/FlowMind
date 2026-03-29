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

## 실제 산출물

- `backend/build.gradle.kts`
- `backend/settings.gradle.kts`
- `backend/gradlew`, `backend/gradlew.bat`
- `backend/src/main/java/com/flowmind/FlowMindApplication.java`
- `backend/src/main/java/com/flowmind/config/FlowMindRuntimeProperties.java`
- `backend/src/main/java/com/flowmind/health/HealthController.java`
- `backend/src/main/resources/application.yml`
- `backend/src/main/resources/db/migration/V1__bootstrap_baseline.sql`
- `backend/src/test/java/com/flowmind/FlowMindApplicationTests.java`
- `backend/README.md`

## 구현 선택 기준

- 구조: `backend/` 하위 단일 모듈
- 빌드 도구: `Gradle`
- Java: `21`
- Spring Boot: `3.x`
- 기본 패키지: `com.flowmind`

## 구현 결과 요약

- `backend/` 단일 모듈 Spring Boot 프로젝트를 생성했다
- Gradle wrapper를 포함해 로컬에서 바로 실행 가능한 구조를 만들었다
- `FlowMindApplication`과 runtime properties 바인딩 구조를 추가했다
- `GET /api/health` 커스텀 endpoint와 actuator health를 노출했다
- 기본 환경에서 DB가 없어도 health 검증이 가능하도록 bootstrap 정책을 조정했다
- PostgreSQL / Flyway / Redis / OpenAI 연동을 위한 설정 키 골격을 마련했다
- Flyway baseline migration 파일을 추가했다

## 검증 결과

- `gradle test` 성공
- `GET /api/health` 응답 확인
- `GET /actuator/health` 응답 확인

## 남은 과제

- deterministic core를 위한 package 경계 확장
- dispatch API와 첫 시나리오 구현
- conversation log / dispatch trace 저장 구조 추가
- local DB / Redis 기동 자동화 전략 정리

## 선행 참고 문서

- [plan-01-project-bootstrap.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-01-project-bootstrap.md)
- [design-bootstrap.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/design-bootstrap.md)
- [plan-02-runtime-config.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-02-runtime-config.md)
- [plan-03-storage-foundation.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-03-storage-foundation.md)
- [plan-04-health-and-observability.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-04-health-and-observability.md)

## 진행 상태

- `completed`
