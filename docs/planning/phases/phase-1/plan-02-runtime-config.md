# Phase 1 Plan 02

## 이름

Runtime Config

## 목표

애플리케이션이 실행 가능한 기본 설정 체계를 만든다.

## 범위

- `application.yml` 구성
- 환경변수 키 정리
- local/dev 구분 전략 정의
- OpenAI, DB, Redis용 자리 구성
- 기본 설정값 정책 정의

## 완료 기준

- 실행에 필요한 설정 키가 정리된다
- 환경별 오버라이드 전략이 정리된다
- 민감한 값은 환경 변수로 분리된다

## 리스크

- 설정을 너무 많이 미리 정의하면 관리 복잡도 증가
- local/dev/prod 경계가 애매하면 이후 운영 혼선 발생

## 보완 필요 항목

- `application-local.yml` 도입 여부
- timeout / threshold 기본값 초기안
- API key 관리 방식 명문화
