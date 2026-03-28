# FlowMind 라피치 공고 대응 실행 계획

## 1. 문서 목적

이 문서는 `FlowMind`를 라피치 채용 공고에 맞는 포트폴리오 프로젝트로 발전시키기 위한 구현 우선순위와 단계별 계획을 정리한 문서다.

핵심 원칙은 단순하다.

- 공고와 직접 연결되는 기능부터 구현한다
- 설명하기 어려운 기능보다 증명 가능한 기능을 우선한다
- "예쁘게 많이 만든 프로젝트"보다 "실무형 구조를 끝까지 동작시킨 프로젝트"를 목표로 한다

## 2. 최종 목표

최종 목표는 아래 한 문장으로 정리할 수 있다.

`Intent 기반 시나리오 처리와 LLM fallback을 결합한 Java 기반 고객 응대 플랫폼 MVP를 구현하고, 운영 로그와 품질 지표까지 확인 가능한 상태로 만드는 것`

이 목표를 만족하면 라피치 공고의 핵심 문장을 대부분 자연스럽게 설명할 수 있다.

## 3. 구현 우선순위

### 우선순위 1. Java 백엔드 증빙 확보

가장 먼저 필요한 것은 `Spring Boot` 기반 서버다.

구현 항목:

- Spring Boot 프로젝트 생성
- 기본 health API
- 대화 요청용 `POST /api/conversations/dispatch`
- PostgreSQL, Redis, Flyway 연결

이 단계의 목적은 "Java 기반 웹 서비스 개발" 항목을 실제 코드로 증명하는 것이다.

### 우선순위 2. 인텐트 기반 대화 구조 구현

다음으로 공고 핵심인 `Intent`, `Entity`, `Slot`, `Scenario`를 실제로 돌아가게 해야 한다.

구현 항목:

- Intent catalog
- Utterance 예문 데이터
- Entity 추출 규칙
- Slot schema
- SessionContext 저장
- Scenario executor

최소 목표:

- `요금 조회`
- `요금표 발송`
- `결제 방법 변경`

이 중 1개는 반드시 slot filling까지 포함해야 한다.

### 우선순위 3. LLM fallback 구현

하이브리드 구조의 핵심 증빙이다.

구현 항목:

- `LlmGateway`
- prompt file 또는 prompt template 관리
- low-confidence route
- masked context 기반 prompt augmentation
- structured output contract

이 단계가 완료되면 "인텐트 기반 시나리오와 LLM 응답을 결합한 대화 구조 설계"를 실제 구현 경험으로 설명할 수 있다.

### 우선순위 4. 운영 로그와 품질 지표 구현

라피치 공고는 단순 기능 구현보다 운영 품질 고도화에 가깝다. 그래서 로그와 분석이 반드시 필요하다.

구현 항목:

- conversation log 저장
- dispatch trace 저장
- fallback reason 저장
- prompt version 저장
- latency 저장
- 간단한 통계 조회 API

핵심 지표:

- intent accuracy sample
- scenario route ratio
- llm fallback ratio
- slot completion rate
- average latency

### 우선순위 5. 콜봇/음성봇 연결성 보강

실제 전화 인프라 연동까지 가지 않더라도, 콜봇 업무와 연결되는 흔적을 보여줘야 한다.

구현 항목:

- STT 입력 텍스트를 전제로 한 대화 처리 API
- 채널 타입 `VOICE`, `CHAT` 구분
- 음성봇용 짧은 응답 포맷 옵션
- 전화번호, 인증, 기간 조회 같은 음성봇형 시나리오 예시

가능하다면 추가:

- 외부 STT 결과 JSON을 받아 dispatch 하는 mock endpoint

## 4. 단계별 실행 계획

## Phase 1. Backend Foundation

목표는 `Java 기반 웹 서비스` 증빙을 빠르게 만드는 것이다.

작업:

- backend 디렉터리 생성
- Spring Boot 초기 설정
- Gradle 또는 Maven 설정
- PostgreSQL / Redis / Flyway 연결
- 기본 엔티티와 마이그레이션 정의

완료 기준:

- 서버가 실행된다
- DB 마이그레이션이 적용된다
- health API가 응답한다

## Phase 2. Intent and Scenario MVP

목표는 `인텐트 기반 대화 시나리오`가 실제로 동작하는 것이다.

작업:

- `Intent`, `Utterance`, `Entity`, `Slot`, `SessionContext` 모델 정의
- 간단한 intent classifier 구현
- slot filling 로직 구현
- billing inquiry 시나리오 구현
- dispatch API 구현

완료 기준:

- 사용자가 `"요금 조회"`를 보내면 시나리오로 진입한다
- 전화번호와 기간이 없으면 추가 질문을 한다
- 필요한 슬롯이 모이면 조회 결과를 반환한다

## Phase 3. Hybrid LLM Route

목표는 `시나리오 + LLM fallback` 구조를 실제로 증명하는 것이다.

작업:

- `resources/prompts/` 구성
- fallback prompt 작성
- OpenAI API 연동
- low-confidence routing
- structured LLM output 파싱

완료 기준:

- 애매한 입력이 LLM route로 간다
- prompt version이 기록된다
- 응답 결과와 route reason이 저장된다

## Phase 4. Ops and Analytics

목표는 `품질 개선과 운영 리포트`의 기초를 만드는 것이다.

작업:

- conversation log API
- route ratio 조회 API
- fallback rate 조회 API
- 실패 발화 수집
- prompt version별 간단 비교

완료 기준:

- 최소 3~5개 지표를 조회할 수 있다
- 어떤 입력이 자주 fallback 되는지 볼 수 있다

## Phase 5. Portfolio Packaging

목표는 프로젝트를 지원서에서 설명 가능한 형태로 정리하는 것이다.

작업:

- 아키텍처 다이어그램
- 대표 시나리오 데모 시퀀스
- API 문서 정리
- 핵심 테이블 설명
- 개선 포인트 정리

완료 기준:

- README만 읽어도 프로젝트 목적과 구조를 이해할 수 있다
- 면접에서 5분 내 설명 가능한 수준으로 정리된다

## 5. 포트폴리오 관점 필수 산출물

아래 산출물은 반드시 남겨야 한다.

- 실행 가능한 Spring Boot 프로젝트
- 시나리오 1개 이상의 실제 동작 GIF 또는 화면 캡처
- Intent/Entity/Slot 정의 문서
- Prompt 예시와 fallback 흐름 설명
- conversation log 예시
- 지표 조회 결과 예시

이 산출물이 있어야 프로젝트가 단순 아이디어가 아니라 구현 경험으로 인정된다.

## 6. 권장 MVP 범위

라피치 공고 대응용으로는 아래 범위가 가장 현실적이다.

- 백엔드 중심 MVP
- 관리자 UI는 최소화
- 시나리오 빌더는 실제 편집기보다 JSON 또는 간단한 관리 구조로 시작
- 도메인은 `통신 요금 문의` 하나로 집중
- 음성봇형 시나리오를 반영하되 STT 자체 구현은 제외

즉, 처음부터 모든 것을 만들지 말고 "통신사 콜봇의 요금 조회 흐름을 하이브리드 구조로 안정적으로 처리한다"를 먼저 완성하는 것이 맞다.

## 7. 구현 순서 제안

실제 작업 순서는 아래가 적절하다.

1. Spring Boot 백엔드 초기화
2. DB 스키마와 핵심 도메인 모델 작성
3. dispatch API와 session context 구현
4. billing inquiry 시나리오 구현
5. intent classifier와 entity 추출 구현
6. slot filling 구현
7. OpenAI fallback 구현
8. conversation log와 trace 저장
9. 간단한 지표 조회 API 추가
10. 문서와 데모 정리

## 8. 하지 말아야 할 것

초기에는 아래 항목을 욕심내지 않는 것이 좋다.

- 복잡한 시나리오 빌더 프론트엔드
- full RAG 파이프라인
- 다중 모델 provider
- 자동 평가 체계 전체
- 실시간 음성 스트리밍

이 항목들은 프로젝트 완성도를 높여주지만, 현재 목적은 라피치 공고에 맞는 실무형 핵심 역량 증명이다.

## 9. 성공 판정 기준

이 계획이 성공했다고 보려면 아래 질문에 모두 답할 수 있어야 한다.

- Java 기반 웹 서비스인가
- 인텐트 기반 시나리오가 실제로 동작하는가
- LLM fallback이 실제로 연결되는가
- 대화 로그와 품질 지표가 남는가
- 콜봇/음성봇 업무 흐름으로 설명 가능한가

이 다섯 가지가 충족되면 FlowMind는 라피치 공고 대응용 포트폴리오 프로젝트로 충분한 설득력을 갖는다.
