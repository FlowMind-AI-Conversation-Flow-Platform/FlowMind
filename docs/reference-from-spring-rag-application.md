# FlowMind 참고 문서: `spring-rag-application`에서 배울 점

## 1. 문서 목적

이 문서는 `C:\Users\ggg99\Desktop\ex\spring-rag-application` 프로젝트를 검토한 뒤, FlowMind에 참고할 만한 실무형 LLM 시스템 설계 포인트를 정리한 문서다.

중요한 점은 이 프로젝트를 그대로 복제하는 것이 아니다.

FlowMind는 `Intent + Scenario + LLM Fallback` 중심의 하이브리드 대화 플랫폼이고, 참고 프로젝트는 `RAG 중심 업무형 AI 시스템`이다. 따라서 구조는 다르지만, 실무 운영 관점에서 배울 만한 설계 원칙은 많다.

## 2. 먼저 결론

이 프로젝트를 보고 난 뒤 FlowMind에 바로 반영할 가치가 큰 것은 아래 다섯 가지다.

1. 프롬프트를 코드 밖 파일로 분리하는 방식
2. 설정값을 코드 상수가 아니라 운영 가능한 설정으로 두는 방식
3. 파이프라인 단계별 trace를 남기는 관측 구조
4. 모델 접근을 추상화해서 provider 교체를 가능하게 하는 구조
5. 긴 처리 과정을 작은 단계로 나눠 상태를 관리하는 방식

반대로 지금 FlowMind MVP에는 바로 필요하지 않은 것은 아래다.

- full RAG ingestion pipeline
- 문서 업로드/청킹/임베딩 전체 파이프라인
- 다중 LLM provider 운영
- 자동 평가 체계의 전체 구현

즉, 구조는 가볍게 시작하되 실무형 운영 관점은 초기에 가져오는 것이 좋다.

## 3. 참고 프로젝트의 성격

`spring-rag-application`은 단순한 데모가 아니라, 운영형 RAG 애플리케이션에 가깝다.

보이는 특징:

- 문서 업로드와 파싱
- chunking 및 embedding
- 벡터 검색과 키워드 검색의 혼합
- query routing
- prompt 파일 분리
- 모델 관리 레이어
- pipeline trace 저장
- dashboard 및 evaluation
- auth, audit, rate limit

즉, "LLM을 그냥 호출하는 앱"이 아니라 "LLM 기반 기능을 운영 가능한 서비스로 만든 구조"라는 점이 중요하다.

## 4. FlowMind가 특히 참고해야 할 부분

### 4-1. 프롬프트를 파일로 분리한 구조

참고 프로젝트는 `backend/src/main/resources/prompts/` 아래에 프롬프트를 분리해 두고, `PromptLoader`로 로드한다.

이 방식의 장점:

- 프롬프트 수정이 코드 로직과 분리된다
- 버전 추적이 쉽다
- 실험 대상이 명확하다
- 나중에 DB 기반 PromptOps로 확장하기 쉽다

FlowMind에도 이 원칙은 매우 잘 맞는다.

왜냐하면 FlowMind는 초기에 DB 기반 PromptTemplate까지 한 번에 가지 않더라도, 최소한 아래 둘은 분리해야 하기 때문이다.

- 시스템 프롬프트
- fallback 용 프롬프트

### FlowMind 적용 제안

초기에는 아래처럼 시작하면 된다.

- `resources/prompts/dispatcher-fallback.txt`
- `resources/prompts/clarify.txt`
- `resources/prompts/complaint-response.txt`

그리고 나중에 `prompt_template` 테이블로 확장한다.

즉, 처음부터 문자열 하드코딩을 피하고, 프롬프트를 운영 자산처럼 다루는 습관을 가져가는 것이 좋다.

## 5. 설정을 운영 가능한 값으로 분리한 점

참고 프로젝트는 `application.yml`에 기본 설정을 두고, 일부는 `SettingsService`를 통해 DB에서 읽을 수 있게 구성했다.

예:

- chunking mode
- chunk size
- overlap
- embedding batch size
- concurrency

이런 구조는 FlowMind에도 그대로 응용할 수 있다.

### FlowMind에서 운영 설정으로 두면 좋은 것

- intent confidence 기본 threshold
- fallback threshold
- 세션 TTL
- 대화 이력 최대 길이
- 최대 prompt context 길이
- LLM timeout
- route 정책 옵션

이 값을 코드 상수로 박아두면 튜닝할 때마다 배포가 필요해진다.

따라서 FlowMind도 초기부터 `설정은 설정으로 분리`하는 철학을 가져가는 것이 좋다.

## 6. 단계별 trace와 observability 구조

참고 프로젝트의 `PipelineTracer`는 파이프라인 단계 결과를 JSON으로 로그에 남기고, DB에도 저장한다.

이건 FlowMind에 특히 중요하다.

왜냐하면 FlowMind의 핵심은 "무슨 답을 했는가"보다 "왜 그 route로 갔는가"이기 때문이다.

### FlowMind에서 trace로 남겨야 할 것

- 입력 문장
- 추정 intent 후보
- top1 / top2 score
- confidence 계산 결과
- 현재 session context 요약
- route 결정 사유
- scenario node 이동 정보
- llm fallback 여부
- prompt version
- latency

이 정보가 있어야 나중에 아래 질문에 답할 수 있다.

- 왜 scenario로 안 갔는가
- 왜 잘못된 intent로 갔는가
- 어떤 입력이 fallback을 과도하게 유발하는가
- 어떤 prompt 버전에서 품질이 나빠졌는가

즉, FlowMind에서는 `conversation_log` 외에 `dispatch_trace` 성격의 구조도 나중에 고려할 가치가 있다.

## 7. 모델 접근 추상화

참고 프로젝트는 `ModelClientProvider`를 두고, 목적별로 적절한 모델 client를 꺼내는 구조를 사용한다.

이 방식은 지금 FlowMind MVP에서는 조금 과할 수 있다. 하지만 아이디어 자체는 매우 좋다.

### 바로 가져갈 수 있는 핵심

FlowMind도 LLM 호출을 아래처럼 직접 코드 여기저기에서 하지 않는 것이 좋다.

- 컨트롤러에서 직접 호출 금지
- 서비스마다 제각각 호출 금지
- 모델명 하드코딩 금지

대신 최소한 아래 하나는 있어야 한다.

- `LlmGateway`

이 게이트웨이는 아래 역할을 맡는다.

- 모델 선택
- timeout 처리
- 공통 예외 처리
- token usage 수집
- prompt / response logging

즉, 참고 프로젝트처럼 완전한 multi-provider 구조는 아니더라도, "LLM 접근은 단일 진입점으로"라는 원칙은 FlowMind에 바로 적용해야 한다.

## 8. 긴 과정을 단계로 분리하는 방식

참고 프로젝트의 ingestion pipeline은 아래처럼 단계를 명확히 나눈다.

1. 문서 파싱
2. 부모 청크 분리
3. 자식 청크 분리
4. 임베딩 생성
5. 저장
6. 상태 갱신

이 구조는 RAG에만 해당하는 것이 아니다.

FlowMind의 dispatcher도 같은 식으로 나누는 것이 좋다.

### FlowMind dispatcher 권장 단계

1. 입력 정규화
2. context 로드
3. intent/entity 분석
4. confidence 계산
5. route 결정
6. scenario 실행 또는 llm 호출
7. 응답 조립
8. trace / log 저장

이렇게 분리하면 테스트가 쉬워지고, 어디서 실패했는지 찾기 쉬워진다.

## 9. 참고 프로젝트에서 보이는 실무적 태도

이 프로젝트의 가장 좋은 점은 "기능 구현"보다 "운영 가능성"을 먼저 생각했다는 데 있다.

보이는 흔적:

- JWT auth
- role 기반 접근 제어
- audit log
- rate limit
- dashboard
- evaluation
- trace 저장
- prompt 파일 분리

이건 FlowMind에도 중요한 메시지를 준다.

즉, LLM 시스템은 단순히 모델을 붙이는 것이 아니라 아래까지 포함해야 실무형이 된다.

- 누가 썼는가
- 얼마나 썼는가
- 어떤 경로로 처리됐는가
- 왜 실패했는가
- 무엇을 바꾸면 좋아지는가

## 10. FlowMind에 반영할 실무 원칙

아래 원칙은 이 참고 프로젝트에서 가져와도 좋다.

### 원칙 1. 프롬프트는 코드 상수가 아니라 관리 대상이다

FlowMind에서도 fallback 프롬프트를 코드 내부 문자열로 두지 말고, 파일 또는 템플릿 레이어로 분리한다.

### 원칙 2. 파이프라인은 단계별로 분리하고 기록한다

dispatcher 내부 로직을 한 메서드에 몰아 넣지 말고, 단계별 컴포넌트로 나눈다.

### 원칙 3. 운영 튜닝 값은 설정으로 빼야 한다

threshold, timeout, session TTL, fallback 정책은 설정으로 다뤄야 한다.

### 원칙 4. LLM 호출은 단일 게이트웨이로 묶는다

나중에 모델 변경이나 비용 최적화를 하려면 진입점이 하나여야 한다.

### 원칙 5. 로그는 결과보다 판단 과정을 남겨야 한다

FlowMind의 핵심 가치는 결과 메시지보다 routing 판단에 있다.

## 11. FlowMind에 바로 도입하지 말아야 할 것

참고 프로젝트가 잘 만들었다고 해서 FlowMind 초기에 모두 넣으면 오히려 느려진다.

지금은 아래를 바로 도입하지 않는 것이 좋다.

### full RAG ingestion

FlowMind는 아직 문서 검색형 제품이 아니다. 지금 당장은 문서 chunking, embedding, reindexing이 핵심이 아니다.

### 복잡한 multi-model registry

초기에는 OpenAI 한 경로만으로 충분하다.

### 자동 평가 전체 체계

초기에는 사용자 평점과 fallback 비율만으로도 충분히 개선 가능하다.

### 과도한 agent 구조

FlowMind는 우선 deterministic dispatcher가 중심이다. 너무 이른 agentification은 복잡도만 높인다.

## 12. FlowMind 기준 추천 채택 항목

초기 채택 추천:

- prompt 파일 분리
- llm gateway 단일화
- dispatch trace 구조
- 설정값 분리
- 단계별 서비스 구성

중기 채택 추천:

- 운영자용 settings 관리
- token / latency 메트릭
- prompt version 실험
- 평가 지표 대시보드

후기 채택 추천:

- 문서 기반 fallback이 필요할 때만 RAG 일부 도입
- 모델 다변화가 필요할 때 model registry 확장

## 13. FlowMind용 실행 제안

이 참고 프로젝트를 본 뒤, FlowMind의 초기 구현에서는 아래를 우선 반영하는 것이 좋다.

1. `prompts/` 디렉터리 도입
2. `LlmGateway` 도입
3. `DispatchTrace` 개념 설계
4. `application.yml` + 운영 설정 분리
5. dispatcher를 단계형 서비스로 설계

이 다섯 개는 RAG를 도입하지 않아도 충분히 적용 가능하고, 실무형 품질에 직접 기여한다.

## 14. 최종 정리

`spring-rag-application`에서 FlowMind가 배워야 할 핵심은 RAG 기술 자체보다, `LLM 기능을 운영 가능한 시스템으로 다루는 방식`이다.

FlowMind는 지금 단계에서 RAG 전체를 가져올 필요는 없지만, 아래 철학은 반드시 가져가는 것이 좋다.

- 프롬프트를 자산으로 관리할 것
- 파이프라인 판단 과정을 추적 가능하게 만들 것
- 설정값을 운영 가능한 구조로 둘 것
- LLM 호출을 단일 게이트웨이로 통제할 것
- 기능보다 운영성을 먼저 고려할 것
