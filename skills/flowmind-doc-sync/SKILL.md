---
name: flowmind-doc-sync
description: "FlowMind 문서 동기화 스킬. 코드/구현 상태 변경, phase 상태 업데이트, 실행 계획 정리, AGENT/README 포인터 갱신이 필요할 때 반드시 사용한다."
---

# FlowMind Doc Sync

## 목적
코드 변경 이후 `docs/`, `README`, `AGENT` 문서 상태를 실제 구현과 일치시킨다.

## 실행 절차
1. 변경된 코드/설정/테스트 결과를 수집한다.
2. 영향 문서를 식별한다.
- `docs/planning/PHASES.md`
- `docs/planning/exec-plans/*`
- `README.md`, `AGENT.md`
3. 상태값과 설명을 최신화한다.
4. 문서 간 링크와 용어를 정합성 있게 맞춘다.

## 문서 원칙
- 추상 설명보다 실행 기준을 남긴다.
- 완료/진행중 상태는 근거가 있을 때만 변경한다.
- 모르는 내용은 추측하지 않고 "확인 필요"로 남긴다.

## 체크리스트
- [ ] 코드 상태와 phase 상태가 맞는가
- [ ] 실행 계획(active/completed) 분류가 맞는가
- [ ] README의 현재 상태 서술이 최신인가
- [ ] AGENT 포인터와 변경 이력이 반영되었는가
