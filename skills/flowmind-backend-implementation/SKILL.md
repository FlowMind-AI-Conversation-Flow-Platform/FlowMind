---
name: flowmind-backend-implementation
description: "FlowMind backend 구현 전용 스킬. Spring Boot 코드 작성, deterministic core 구현, API/설정 변경, 테스트 보강, 회귀 수정 요청이 있으면 이 스킬을 사용한다."
---

# FlowMind Backend Implementation

## 목적
`backend/`에서 Deterministic Core 중심 기능을 안전하게 구현한다.

## 실행 절차
1. 변경 대상 범위를 `backend/` 내 파일로 고정한다.
2. 기존 구성(`application.yml`, runtime properties, health baseline)을 깨지 않게 설계한다.
3. 작은 단위로 구현하고 테스트를 바로 수행한다.
4. 변경 파일, 테스트 결과, 남은 리스크를 보고한다.

## 구현 원칙
- 현재 우선순위는 `Phase 2 > Deterministic Core`다.
- 과도한 agentification이나 조기 복잡도 확장은 피한다.
- LLM 연동은 fallback 경계가 필요한 시점에서만 추가한다.
- 스키마 변경 시 Flyway와 코드 반영을 함께 맞춘다.

## 체크리스트
- [ ] API 계약과 DTO가 일치하는가
- [ ] 구성값은 환경변수로 오버라이드 가능한가
- [ ] 실패 응답/예외 경로를 다뤘는가
- [ ] 테스트로 회귀 리스크를 막았는가
