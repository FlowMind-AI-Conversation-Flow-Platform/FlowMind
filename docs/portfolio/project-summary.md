# FlowMind Project Summary

## 한 줄 소개

FlowMind는 `Intent 기반 시나리오 처리`와 `LLM fallback`을 결합한 하이브리드 대화 플랫폼이다.

## 어떤 문제를 해결하는가

고객 응대 시스템은 보통 두 가지 문제를 가진다.

- 규칙 기반 시스템은 안정적이지만 유연성이 부족하다
- LLM 단독 시스템은 유연하지만 통제와 일관성이 어렵다

FlowMind는 이 두 문제를 동시에 풀기 위해 설계됐다.

- 명확한 요청은 deterministic scenario로 처리
- 애매한 요청은 context-aware LLM fallback으로 처리
- 모든 판단과 결과는 운영 데이터로 남겨 개선

## 왜 이 프로젝트가 좋은 포트폴리오가 되는가

- Java/Spring Boot 기반 백엔드 설계를 보여줄 수 있다
- 대화 시스템 모델링 역량을 설명할 수 있다
- 단순 응답이 아니라 운영 가능한 구조를 설계한 점을 보여줄 수 있다
- prompt, route, trace, metrics를 함께 다루는 실무형 접근을 설명할 수 있다

## 현재 상태

- 문서 구조와 phase 계획이 정리돼 있다
- `backend/` 하위 Spring Boot bootstrap 구현이 완료됐다
- health endpoint, actuator health, Gradle test까지 확인됐다
- 다음 단계는 deterministic core와 LLM fallback 구현이다

## 나중에 포트폴리오 본문에 넣을 핵심 문장

FlowMind는 "모든 요청을 LLM으로 처리하는 챗봇"이 아니라, `규칙 기반 시나리오 엔진`과 `생성형 보완 응답`을 분리해 운영 안정성과 사용자 경험을 동시에 확보하려는 프로젝트다.
