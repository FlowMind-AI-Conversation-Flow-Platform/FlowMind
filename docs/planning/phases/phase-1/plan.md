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

- 상태: `pending`
- 관련 execution plan:
  - [spring-boot-foundation.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/exec-plans/active/spring-boot-foundation.md)
- 세부 계획:
  - [plan-01-project-bootstrap.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-01-project-bootstrap.md)
  - [plan-02-runtime-config.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-02-runtime-config.md)
  - [plan-03-storage-foundation.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-03-storage-foundation.md)
  - [plan-04-health-and-observability.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/phases/phase-1/plan-04-health-and-observability.md)

## 리스크

- 초기 패키지 구조를 과하게 설계할 위험
- MVP 범위를 넘어서 인프라 작업이 비대해질 위험

## 보완 필요 항목

- 패키지 네이밍 규칙 확정
- 설정값 파일 구조 확정
- local dev 실행 기준 문서화
