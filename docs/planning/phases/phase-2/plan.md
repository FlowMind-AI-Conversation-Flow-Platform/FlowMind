# Phase 2 Plan

## 이름

Deterministic Core

## 목표

금융 보이스옵스 도메인에서 Intent 기반 시나리오 실행의 최소 완성본을 만든다.

## 범위

- 금융 도메인 Intent / Utterance / Entity / Slot 모델 정의
- `ACCOUNT_INFO_INQUIRY`, `TRANSACTION_HISTORY_INQUIRY`, `CARD_LOST_OR_LIMIT` 3개 Intent 우선 구현
- SessionContext 유지
- Dispatch API 구현
- slot filling 흐름 구현
- 시나리오 3개의 deterministic 경로 구현

## 완료 기준

- 본인확인 후 계좌 정보 안내 시나리오가 끝까지 처리된다
- 거래내역 조회에서 누락 슬롯 후속 질문이 동작한다
- 카드 분실/한도 요청에서 정책 분기가 동작한다
- session context가 대화 흐름을 유지한다

## 현재 상태

- 상태: `in_progress`

## 리스크

- 금융 인증/보안 정책을 과소 모델링하면 실제 운영 시나리오와 괴리될 수 있음
- intent 분류와 scenario 실행 경계가 불명확해질 수 있음
- 모델링을 과하게 하면 구현 속도가 느려질 수 있음

## 보완 필요 항목

- 금융 도메인 slot schema 확정 (`고객식별`, `계좌`, `기간`, `요청유형`)
- 본인확인 정책 레벨 확정 (MVP에서 허용할 인증 수준)
- 카드 분실/한도 시나리오의 상담원 이관 기준 확정
