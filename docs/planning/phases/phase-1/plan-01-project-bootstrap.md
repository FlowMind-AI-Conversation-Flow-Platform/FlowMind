# Phase 1 Plan 01

## 이름

Project Bootstrap

## 목표

FlowMind 백엔드 프로젝트의 기본 뼈대를 만든다.

이 문서는 `phase 관점`에서 bootstrap의 범위와 기준을 정의한다.
실제 이번 작업의 실행 단위는 아래 execution plan과 연결한다.

- [spring-boot-foundation.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/exec-plans/completed/spring-boot-foundation.md)
- 구현 결과:
  - `backend/` Spring Boot 프로젝트 생성 완료
  - Gradle wrapper 포함
  - health endpoint 및 actuator health 확인 완료

## 범위

- `backend/` 디렉터리 생성
- Spring Boot 프로젝트 초기화
- Gradle 또는 Maven 구조 확정
- 기본 패키지 네이밍 결정
- 최소 실행 엔트리포인트 추가

## 결정 사항

### 빌드 도구

- `Gradle` 사용
- 이유:
  - 레퍼런스 프로젝트도 Gradle 기반이다
  - Spring Boot 초기화와 의존성 관리가 단순하다
  - 이후 멀티모듈로 갈 가능성이 있어도 확장 여지가 충분하다

### Java / Spring Boot 기준

- `Java 21`
- `Spring Boot 3.x`

### 초기 구조

- 루트는 문서 중심 구조를 유지한다
- 구현은 `backend/` 단일 모듈로 시작한다
- 프론트엔드는 Phase 1의 주범위가 아니다

### 초기 패키지 네임 제안

- 1안: `ai.flowmind`
- 2안: `com.flowmind`

현 시점에서는 `ai.flowmind`보다 보수적인 `com.flowmind`가 무난하다.
특별한 브랜딩 이유가 없다면 우선 `com.flowmind`를 사용한다.

## 권장 최소 디렉터리 구조

```text
backend/
  build.gradle.kts
  settings.gradle.kts
  src/
    main/
      java/com/flowmind/
        FlowMindApplication.java
        config/
        common/
        health/
      resources/
        application.yml
        db/migration/
    test/
      java/com/flowmind/
```

## 초기 의존성 기준

반드시 포함:

- `spring-boot-starter-web`
- `spring-boot-starter-actuator`
- `spring-boot-starter-data-jpa`
- `flyway-core`
- `flyway-database-postgresql`
- `postgresql`
- `spring-boot-starter-test`

선택 포함:

- `spring-boot-starter-data-redis`
  - Redis를 Phase 1부터 실제 연결할 경우에만 추가

지금은 제외:

- security
- websocket
- OpenAI SDK
- template renderer
- document parsing

이 항목들은 bootstrap보다 다음 phase에서 도입하는 편이 낫다.

## 실행 순서 제안

1. `backend/` 디렉터리 생성
2. Gradle 기반 Spring Boot 초기 프로젝트 생성
3. `com.flowmind` 패키지 구조 생성
4. `FlowMindApplication` 추가
5. health endpoint 또는 actuator 설정
6. `application.yml`과 `db/migration/` 경로 준비
7. build / run / health check 검증

## 예상 명령 수준

실제 구현 시 최소한 아래 수준의 검증이 가능해야 한다.

- `./gradlew bootRun` 또는 동등 명령
- `./gradlew test`
- health endpoint 호출 확인

Windows 환경에서는 `gradlew.bat` 기준으로 대응한다.

## 완료 기준

- 백엔드 프로젝트가 독립적으로 빌드 가능하다
- 애플리케이션 시작 클래스가 존재한다
- 이후 설정/스토리지 작업이 자연스럽게 이어진다
- `GET /actuator/health` 또는 동등한 health endpoint로 실행 확인이 가능하다

## 검증 기준

- `backend`에서 애플리케이션이 실행된다
- 최소 한 번의 build 또는 test task가 통과한다
- health endpoint가 응답한다
- 기본 설정 파일과 migration 경로가 존재한다

## 리스크

- 초기 구조를 과하게 잡으면 변경 비용 증가
- 패키지 네이밍을 서둘러 정하면 나중에 재정리 필요
- Redis, 보안, LLM 의존성을 너무 일찍 넣으면 bootstrap 목적이 흐려질 수 있음

## 보완 필요 항목

- deterministic core 진입 시 패키지 확장 기준
- DB / Redis 실제 연결 시점
- local profile 분리 여부

## 다음 연결 문서

- [design-bootstrap.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/design-bootstrap.md)
- [spring-boot-foundation.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/exec-plans/completed/spring-boot-foundation.md)
