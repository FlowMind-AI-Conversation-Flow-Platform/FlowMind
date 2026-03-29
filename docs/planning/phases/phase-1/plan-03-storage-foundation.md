# Phase 1 Plan 03

## 이름

Storage Foundation

## 목표

PostgreSQL, Redis, Flyway 기반 저장소 골격을 준비한다.

## 범위

- PostgreSQL 연결 설정
- Redis 연결 여부 및 사용 목적 명시
- Flyway baseline 전략 정의
- 초기 migration 파일 구조 정의

## 완료 기준

- DB 연결 구조가 준비된다
- Flyway migration 경로가 정리된다
- Redis 사용 목적이 명확히 문서화된다

## 현재 반영 상태

- PostgreSQL datasource 기본 키와 driver 설정이 추가됐다
- Flyway migration 경로와 baseline migration 파일이 추가됐다
- Redis는 설정 자리만 열어두고 기본 비활성 상태로 유지한다

## 리스크

- Redis를 너무 일찍 도입하면 MVP가 느려질 수 있음
- 첫 migration 범위를 과하게 잡을 위험

## 보완 필요 항목

- Redis를 Phase 1에 실제 연결할지 결정
- baseline migration에 포함할 최소 테이블 범위 확정
- Docker Compose 도입 시점 결정
