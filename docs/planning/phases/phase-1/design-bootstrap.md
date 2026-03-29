# Phase 1 Bootstrap Design

## 목적

이 문서는 Spring Boot 초기화 작업 전에 필요한 설계 선택지를 짧게 정리한다.

## 구조 선택

### 옵션 A. 루트 단일 백엔드 프로젝트

장점:

- 초기 파일 수가 적다
- 진입 장벽이 낮다

단점:

- 현재 문서 중심 루트 구조와 충돌한다
- 이후 프론트/도구 추가 시 확장성이 떨어진다

### 옵션 B. `backend/` 하위 단일 모듈

장점:

- 현재 문서 중심 루트와 잘 맞는다
- 향후 프론트, infra, scripts 확장에 유리하다
- 레퍼런스 프로젝트 구조와도 유사하다

단점:

- 초기 설정 파일이 약간 늘어난다

권장:

- `backend/` 하위 단일 모듈

## 패키지 구조 선택

초기에는 너무 세분화하지 않는다.

권장 구조:

- `com.flowmind`
- `com.flowmind.config`
- `com.flowmind.common`
- `com.flowmind.health`

지금 단계에서는 `domain`, `dispatcher`, `llm`, `scenario` 패키지를 미리 만들지 않아도 된다.

## 첫 구현 항목

- `FlowMindApplication`
- `HealthController` 또는 actuator health 사용
- `application.yml`
- 빈 migration 디렉터리 또는 baseline migration

## 최소 의존성 제안

- `spring-boot-starter-web`
- `spring-boot-starter-actuator`
- `spring-boot-starter-data-jpa`
- `flyway-core`
- `flyway-database-postgresql`
- `postgresql`
- `spring-boot-starter-test`

선택:

- `spring-boot-starter-data-redis`

## 최소 검증 흐름

1. 애플리케이션 실행
2. health endpoint 확인
3. build 또는 test task 1회 성공 확인
4. migration 경로 존재 확인

## 레퍼런스에서 가져온 점

`spring-rag-application`에서 참고할 부분:

- Gradle 기반 구조
- `backend/` 분리
- `application.yml` 중심 설정
- `db/migration/` 기준 Flyway 구조

지금은 가져오지 않는 부분:

- security
- websocket
- template rendering
- model provider abstraction
- RAG 관련 parsing/search 계층

## 다음 연결 지점

bootstrap이 끝나면 다음 문서로 이어진다.

- [plan-02-runtime-config.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-02-runtime-config.md)
- [plan-03-storage-foundation.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-03-storage-foundation.md)
- [spring-boot-foundation.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/exec-plans/completed/spring-boot-foundation.md)
