# FlowMind Implementation Evidence

## 목적

이 문서는 구현이 진행되면서 반드시 남겨야 할 포트폴리오 증빙 항목을 정리한다.

## 1. 백엔드 기초 증빙

- Spring Boot 프로젝트 구조 캡처
- 실행 화면 또는 health endpoint 응답
- Gradle build 성공 기록
- 현재 확보됨:
  - `backend/` 프로젝트 구조
  - `/api/health` 응답
  - `/actuator/health` 응답
  - `gradle test` 성공

## 2. deterministic core 증빙

- Intent / Entity / Slot 모델 설명
- 대표 시나리오 1개 흐름도
- slot filling 전후 예시
- dispatch API 요청/응답 예시

## 3. LLM fallback 증빙

- fallback 발생 입력 예시
- prompt file 예시
- prompt augmentation에 어떤 context가 들어가는지 예시
- structured output 또는 route reason 예시

## 4. 로그 / trace 증빙

- conversation log 샘플
- dispatch trace 샘플
- fallback reason, prompt version, latency 기록 예시

## 5. 운영 지표 증빙

- scenario route ratio
- llm fallback ratio
- slot completion rate
- 평균 응답 시간

## 6. UI 또는 데모 증빙

- 최소 하나의 시연 이미지 또는 GIF
- 사용자가 질문하고 응답이 돌아오는 end-to-end 흐름

## 구현 후 체크

- 캡처를 남겼는가
- API 예시를 남겼는가
- 로그 샘플을 남겼는가
- 정량 지표를 남겼는가
