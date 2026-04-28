# FlowMind 페이즈 로드맵

이 문서는 상위 요약 로드맵이다.
각 phase의 상세 실행 문서는 [PHASES.md](/C:/Users/ggg99/Desktop/FlowMind/FlowMind/docs/planning/PHASES.md)와 `docs/planning/phases/` 아래에서 관리한다.

## Phase 1. Foundation

목표는 개발 가능한 기본 골격을 만드는 것이다.

- 모노리포 구조 정리
- Spring Boot 백엔드 초기 프로젝트 생성
- 프론트엔드는 문서/관리 화면 수준으로 최소 골격만 준비
- PostgreSQL / Redis / Flyway / Docker Compose 설정
- 공통 문서 정리

완료 기준:

- 로컬에서 백엔드가 우선 실행된다
- DB 마이그레이션이 자동 적용된다
- 기본 API가 응답한다

현재 기준:

- Spring Boot bootstrap과 기본 health API는 완료
- DB 실제 기동 자동화와 domain API는 다음 단계

## Phase 2. Deterministic Core

목표는 규칙 기반 대화 처리의 최소 완성본을 만드는 것이다.

- Intent / Utterance / Entity 모델 정의
- SessionContext 및 슬롯 유지
- Scenario / Node / Edge 모델 정의
- Dispatch API 구현
- 금융 도메인 대표 시나리오 3개 완성
  - 본인확인 후 계좌정보 안내
  - 거래내역 조회
  - 카드 분실/한도 요청

완료 기준:

- 명확한 금융 사용자 요청 3개가 시나리오로 끝까지 처리된다
- 필요한 슬롯이 누락되면 시스템이 후속 질문을 한다

## Phase 3. Hybrid Routing

목표는 low-confidence 요청을 LLM으로 안정적으로 넘기는 것이다.

- prompt file 기반 템플릿 관리 도입
- 필요 시 prompt_template 테이블로 확장 가능한 구조 준비
- Prompt augmentation
- OpenAI Responses API 연동
- structured output 계약
- fallback reason 로깅

완료 기준:

- 애매한 요청이 LLM route로 전달된다
- 어떤 프롬프트와 모델이 사용됐는지 로그에 남는다

## Phase 4. Ops & Analytics

목표는 운영자가 시스템을 개선할 수 있게 만드는 것이다.

- route ratio
- fallback rate
- 평균 응답시간
- 사용자 평점
- unmatched utterance 수집

완료 기준:

- 운영자가 실패 지점과 개선 대상을 식별할 수 있다

## Phase 5. Productization

목표는 포트폴리오와 데모 관점에서 완성도를 높이는 것이다.

- Vercel 프론트 배포
- 백엔드 배포
- README / ARCHITECTURE / API 문서 보완
- 데모 시나리오와 샘플 데이터 준비

완료 기준:

- 외부에서 접속 가능한 데모가 존재한다
- 프로젝트 목적과 구조가 문서만으로 이해된다
