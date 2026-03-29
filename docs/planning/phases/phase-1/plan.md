# Phase 1 Plan

## 이름

Backend Foundation

## 목표

Spring Boot 기반 FlowMind 백엔드의 최소 골격을 만든다.

## 범위

- Spring Boot 프로젝트 구조
- 기본 패키지 구조
- health endpoint
- PostgreSQL / Redis / Flyway 연결 골격
- 설정 파일 구성

## 완료 기준

- 서버가 실행된다
- health endpoint가 응답한다
- DB 마이그레이션 구조가 준비된다
- 이후 dispatch API 작업으로 자연스럽게 이어진다

## 현재 상태

- 상태: `in_progress`
- 구현 완료:
  - `backend/` Spring Boot 단일 모듈 생성
  - Gradle wrapper 포함 빌드 구조 추가
  - `FlowMindApplication`, runtime properties, `HealthController` 추가
  - `application.yml` 및 Flyway migration baseline 추가
  - `GET /api/health`, `GET /actuator/health`, `gradle test` 검증 완료
- 다음 초점:
  - Phase 2와 연결되는 intent / dispatch / session 구조 설계 및 구현
- 관련 execution plan:
  - [spring-boot-foundation.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/exec-plans/completed/spring-boot-foundation.md)
- 세부 계획:
  - [plan-01-project-bootstrap.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-01-project-bootstrap.md)
  - [plan-02-runtime-config.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-02-runtime-config.md)
  - [plan-03-storage-foundation.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-03-storage-foundation.md)
  - [plan-04-health-and-observability.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-04-health-and-observability.md)

## 리스크

- 초기 패키지 구조를 과하게 설계할 위험
- MVP 범위를 넘어서 인프라 작업이 비대해질 위험

## 보완 필요 항목

- deterministic core용 package 경계 초안
- local/dev/prod 설정 분리 시점
- Redis 실제 연결 및 Docker Compose 도입 시점
