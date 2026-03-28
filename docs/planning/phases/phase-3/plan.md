# Phase 3 Plan

## 이름

Hybrid Routing

## 목표

low-confidence 입력을 LLM fallback으로 넘기는 하이브리드 라우팅 구조를 구현한다.

## 범위

- prompt file 기반 템플릿 관리
- `LlmGateway`
- low-confidence route
- prompt augmentation
- structured output 파싱
- fallback reason 저장

## 완료 기준

- 애매한 입력이 LLM route로 전달된다
- prompt version과 fallback reason이 저장된다
- scenario와 llm route를 구분해 추적할 수 있다

## 현재 상태

- 상태: `pending`

## 리스크

- prompt와 route 기준이 불명확하면 fallback 남용 위험
- raw PII가 prompt에 포함될 위험

## 보완 필요 항목

- fallback threshold 기준 확정
- prompt 파일 목록 확정
- output contract 스키마 확정
