# Phase 2 Plan

## 이름

Deterministic Core

## 목표

Intent 기반 시나리오 실행의 최소 완성본을 만든다.

## 범위

- Intent / Utterance / Entity / Slot 모델 정의
- SessionContext 유지
- Dispatch API 구현
- 대표 시나리오 1개 구현
- slot filling 흐름 1개 구현

## 완료 기준

- 명확한 요청이 시나리오로 끝까지 처리된다
- 누락 슬롯이 있으면 후속 질문을 한다
- session context가 대화 흐름을 유지한다

## 현재 상태

- 상태: `pending`

## 리스크

- intent 분류와 scenario 실행 경계가 불명확해질 수 있음
- 모델링을 과하게 하면 구현 속도가 느려질 수 있음

## 보완 필요 항목

- 첫 도메인 intent 목록 확정
- slot schema 확정
- 대표 시나리오 우선순위 결정
