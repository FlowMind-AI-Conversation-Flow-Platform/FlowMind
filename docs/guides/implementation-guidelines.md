# FlowMind 구현 가이드라인

## 1. 목적

이 문서는 지금까지 정리한 전략 문서와 참고 프로젝트 검토 결과를 바탕으로, FlowMind 초기 구현에서 무엇을 바로 채택하고 무엇을 보류할지 정리한 실행 기준 문서다.

## 2. 지금 바로 채택할 것

### 2-1. 프롬프트 분리

초기부터 프롬프트를 코드 문자열로 박아 넣지 않는다.

도입:

- `resources/prompts/` 디렉터리
- fallback, clarify, complaint 대응 프롬프트 분리

이유:

- 수정과 비교가 쉽다
- 나중에 PromptOps로 확장하기 쉽다

### 2-2. LLM Gateway 단일화

LLM 호출은 한 곳으로 모은다.

도입:

- `LlmGateway`

책임:

- 모델 호출
- timeout 처리
- 공통 예외 처리
- token / latency 기록

### 2-3. Dispatcher 단계 분리

dispatcher를 한 메서드에 몰아 넣지 않는다.

권장 단계:

1. 입력 정규화
2. context 로드
3. intent / entity 분석
4. confidence 계산
5. route 결정
6. scenario 실행 또는 llm 호출
7. log / trace 저장

### 2-4. 설정값 분리

코드 상수 대신 설정으로 뺀다.

초기 대상:

- fallback threshold
- session TTL
- context history 길이
- llm timeout
- prompt max length

### 2-5. Trace 중심 로그

응답 결과만 저장하지 말고 판단 과정을 남긴다.

기록 대상:

- top intent
- candidate intents
- confidence
- route reason
- prompt version
- latency

## 3. MVP에서는 보류할 것

초기에 아래를 넣으면 속도가 느려진다.

### 3-1. Full RAG

문서 업로드, 청킹, 임베딩, 벡터 검색 전체 파이프라인은 지금 당장 핵심이 아니다.

### 3-2. Multi-model registry

여러 provider를 동시에 다루는 구조는 나중으로 미룬다.

### 3-3. 자동 평가 체계 전체

초기에는 사용자 평점, fallback rate, latency 정도면 충분하다.

### 3-4. 복잡한 agent 구조

FlowMind 초기 중심은 deterministic dispatcher다.

## 4. 초기 구현 우선순위

### 우선순위 A

- backend / frontend 초기 골격
- PostgreSQL / Redis / Flyway
- Intent / Utterance / Scenario / SessionContext 모델
- Dispatch API

### 우선순위 B

- 대표 시나리오 1개
- Slot filling
- conversation log
- prompt 파일 분리

### 우선순위 C

- LLM fallback
- prompt template 관리
- analytics 기초 지표

## 5. 초기 체크리스트

- 문서 구조 정리 완료
- 브랜치 전략 정리 완료
- phase 기준 개발 순서 정리 완료
- backend / frontend 디렉터리 생성
- docker compose 초안 작성
- 첫 마이그레이션 작성
- 첫 dispatch 플로우 구현

## 6. 한 문장 기준

FlowMind의 초기 구현은 "복잡한 AI 플랫폼 전체"를 만드는 것이 아니라, `Intent + Scenario + LLM Fallback`이 실무형 구조로 한 번 끝까지 동작하는 최소 완성본을 만드는 데 집중한다.
