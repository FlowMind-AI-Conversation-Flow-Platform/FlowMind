# FlowMind 대화 시스템 상세 해설

## 1. 이 문서의 목적

이 문서는 FlowMind를 구현하기 전에, 대화 시스템을 구성하는 핵심 개념이 각각 무엇인지 이해하기 위한 설명서다.

특히 아래 질문에 답하는 데 초점을 둔다.

- Intent는 정확히 무엇인가
- NLP와 NLU는 무엇이 다른가
- Entity와 Slot은 어떻게 다른가
- Scenario는 어떤 역할을 하는가
- LLM은 어디까지 맡고 어디부터 맡지 않는가
- 전체 시스템은 어떤 순서로 동작하는가

## 2. 전체 구조를 먼저 보면

FlowMind는 크게 아래 흐름으로 움직인다.

1. 사용자의 문장을 입력받는다
2. 문장을 전처리하고 해석한다
3. 사용자의 의도와 주요 정보(Entity)를 추출한다
4. 현재 세션과 사용자 정보를 결합해 상황을 판단한다
5. 규칙 기반 시나리오로 처리할지, LLM으로 보낼지 결정한다
6. 응답을 만든다
7. 로그를 남기고 나중에 개선에 활용한다

이 중에서 `2~4번`이 보통 NLU 영역이고, `5번`은 Dispatcher, `6번`은 Scenario 또는 LLM이 담당한다.

## 3. NLP와 NLU는 무엇인가

### NLP

`NLP`는 Natural Language Processing, 즉 자연어 처리다.

쉽게 말하면 사람이 쓰는 문장을 컴퓨터가 다룰 수 있게 만드는 기술 전반을 의미한다.

예:

- 문장 정규화
- 형태소 분석
- 토큰화
- 불용어 처리
- 문장 유사도 계산
- 개체명 인식
- 감성 분석

즉, NLP는 범위가 넓다. 텍스트를 다루는 거의 모든 기술이 포함된다.

### NLU

`NLU`는 Natural Language Understanding, 즉 자연어 이해다.

NLP보다 조금 더 좁은 개념으로, "이 문장이 무슨 뜻인지 이해하는 단계"에 가깝다.

대화 시스템에서는 보통 아래를 NLU라고 본다.

- Intent 분류
- Entity 추출
- Slot filling
- 문맥 해석
- 후속 질문인지 여부 판단

정리하면:

- `NLP`는 텍스트를 처리하는 기술 전체
- `NLU`는 텍스트의 의미를 해석하는 핵심 단계

FlowMind에서는 `NLP`가 기반 기술이고, 그 위에 `NLU 파이프라인`이 올라간다고 보면 된다.

## 4. Intent는 무엇인가

`Intent`는 사용자가 지금 무엇을 하려고 하는지를 표현한 시스템용 이름이다.

사용자는 자연어로 말하지만, 시스템은 결국 "무슨 요청인가"를 정리된 형태로 알아야 한다.

예:

- `"요금 조회하고 싶어요"` -> `BILLING_INQUIRY`
- `"카톡으로 명세서 보내줘"` -> `SEND_BILL_DOCUMENT`
- `"상담원 연결해줘"` -> `TRANSFER_TO_AGENT`

즉, Intent는 사용자의 문장을 업무 단위로 번역한 결과다.

### Intent가 필요한 이유

Intent가 있어야 다음 액션을 고를 수 있다.

- 어떤 시나리오를 실행할지
- 어떤 슬롯이 필요한지
- 어떤 응답 정책을 써야 하는지
- 민감한 작업인지 아닌지

Intent가 없다면 시스템은 모든 문장을 단순 텍스트로만 다뤄야 하고, 구조화된 처리로 연결하기 어렵다.

### FlowMind에서 Intent는 어떻게 쓸까

FlowMind에서는 Intent를 단순 라벨로만 쓰지 않는다.

Intent마다 다음 정보가 붙는다.

- 이름
- 설명
- 기본 confidence threshold
- 필요한 슬롯 목록
- 연결 가능한 시나리오
- 예문 목록

즉, Intent는 단순 분류 결과가 아니라 대화 실행의 출발점이다.

## 5. Utterance는 무엇인가

`Utterance`는 특정 Intent를 대표하는 예시 문장이다.

예:

Intent: `BILLING_INQUIRY`

Utterance:

- `"요금 조회"`
- `"청구 금액 알려줘"`
- `"이번 달 얼마 나왔어"`
- `"지난달 요금 확인하고 싶어"`

Intent 분류기는 사용자의 입력을 이런 예문들과 비교해서 가장 가까운 Intent를 찾는다.

### 왜 중요한가

Intent 분류 성능은 모델보다도 예문 품질에 크게 좌우된다.

- 표현 다양성이 충분한가
- 실제 사용자 문장과 비슷한가
- 중복 Intent와 혼동되지 않는가

FlowMind에서 Data Lab을 두는 이유도 결국 좋은 utterance를 계속 쌓기 위해서다.

## 6. Entity는 무엇인가

`Entity`는 사용자의 문장 안에서 추출해야 하는 핵심 정보다.

예:

- 전화번호
- 계좌번호
- 날짜
- 금액
- 문서 종류
- 채널

문장 `"카톡으로 요금표 보내줘"`에서 추출할 수 있는 Entity 예시는 아래와 같다.

- `CHANNEL = KAKAOTALK`
- `DOCUMENT_TYPE = BILL_TABLE`

문장 `"010-1234-5678로 지난달 요금 조회"`에서 추출할 수 있는 Entity 예시는 아래와 같다.

- `PHONE_NUMBER = 010-1234-5678`
- `PERIOD = LAST_MONTH`

### Entity의 역할

Intent가 "무슨 일을 할지"라면, Entity는 "그 일을 하기 위해 필요한 값"이다.

## 7. Slot은 무엇인가

`Slot`은 시스템이 대화를 진행하기 위해 저장해두는 값이다.

중요한 점은 Entity와 Slot이 완전히 같은 개념은 아니라는 것이다.

### Entity vs Slot

- `Entity`는 문장에서 추출된 정보 조각이다
- `Slot`은 대화 상태 안에 저장된 확정 값이다

예:

사용자 입력:

`"010-1234-5678로 지난달 요금 알려줘"`

여기서 추출되는 Entity:

- `PHONE_NUMBER`
- `PERIOD`

그 후 세션에 저장되는 Slot:

- `customer_phone = 010-1234-5678`
- `billing_period = LAST_MONTH`

즉, Entity는 추출 결과이고 Slot은 상태다.

### Slot Filling이란

어떤 Intent를 처리하려면 필요한 값이 모두 모여야 한다. 이 값을 채워가는 과정을 `Slot Filling`이라고 한다.

예:

Intent: `BILLING_INQUIRY`
Required slots:

- `customer_phone`
- `billing_period`

사용자: `"요금 조회"`

현재 슬롯 상태:

- `customer_phone = 없음`
- `billing_period = 없음`

시스템:

`전화번호를 입력해주세요`

사용자: `"010-1234-5678"`

슬롯 상태:

- `customer_phone = 010-1234-5678`
- `billing_period = 없음`

시스템:

`조회 기간을 알려주세요. 이번 달 또는 지난달 중 선택할 수 있습니다.`

이 과정이 Slot Filling이다.

## 8. Context는 무엇인가

FlowMind에서 Context는 단순 부가 정보가 아니라 분류 정확도를 높이는 핵심 자산이다.

### User Context

사용자 자체의 속성이다.

- 채널
- 언어
- 등급
- 계정 연차

예:

- VIP 고객이면 더 정중한 응답
- 카카오톡 채널이면 문서 발송 가능성 높음

### Session Context

현재 대화 세션 안에서 유지되는 상태다.

- 직전 Intent
- 이전 질문과 응답
- 이미 채운 슬롯
- 미해결 슬롯

예:

직전 대화가 `"요금 조회"`였고 사용자가 `"지난 달은?"`이라고 하면, 새 Intent가 아니라 기간 슬롯 보완으로 해석할 수 있다.

### Domain Context

도메인별 공통 지식이다.

- 통신 / 금융 / 커머스 구분
- 자주 쓰는 Intent
- Entity 패턴
- 정책성 제약

예:

통신 도메인에서는 전화번호와 요금 관련 표현이 많고, 금융 도메인에서는 계좌번호와 이체 관련 표현이 많다.

## 9. Intent Classifier는 무엇을 하는가

Intent Classifier는 사용자 입력을 가장 적절한 Intent로 분류하는 컴포넌트다.

### 기본 동작

1. 입력 문장을 전처리한다
2. 예문들과 비교한다
3. 가장 가까운 Intent 후보를 찾는다
4. 점수와 confidence를 계산한다

### 초기 MVP에서는 어떻게 구현할까

초기에는 복잡한 모델보다 아래 수준이면 충분하다.

- 텍스트 정규화
- 토큰화
- TF-IDF 또는 간단한 임베딩 비교
- cosine similarity

이후 고도화 방향은 아래와 같다.

- Sentence Transformer
- 한국어 특화 임베딩
- 도메인별 리랭킹
- session-aware re-scoring

### confidence는 왜 중요한가

분류 결과가 하나 나온다고 바로 믿으면 안 된다.

예:

- 최고 점수는 높지만 두 번째 후보와 차이가 작을 수 있다
- 점수는 높아도 필요한 슬롯이 전혀 없을 수 있다
- 문장 자체가 불만/감정 표현일 수 있다

그래서 FlowMind의 confidence는 단일 점수보다 아래 조합으로 보는 것이 좋다.

- absolute similarity
- top1 - top2 margin
- 슬롯 충족 정도
- 이전 세션과의 연속성
- 금칙어 / 민감도 / fallback 규칙

## 10. Scenario는 무엇인가

Scenario는 Intent가 결정된 뒤 실제 업무 흐름을 실행하는 구조다.

FlowMind에서는 Scenario를 노드 그래프로 본다.

### 왜 그래프 구조인가

대화는 항상 일직선이 아니다.

- 조건 분기
- 재질문
- 실패 처리
- 사람 연결

같은 흐름이 필요하기 때문이다.

그래서 `next_node_id` 하나만 두는 단순 구조보다, 노드와 엣지를 분리한 그래프 구조가 더 적합하다.

### Scenario 안의 대표 노드 유형

- `START`
- `INTENT_MATCH`
- `COLLECT_SLOT`
- `CHECK_CONDITION`
- `ACTION`
- `MESSAGE`
- `END`

### Scenario의 역할

Scenario는 LLM처럼 답을 "생성"하지 않는다. 미리 정한 절차를 "실행"한다.

즉:

- 입력을 받고
- 부족한 정보를 물어보고
- 액션을 호출하고
- 응답 템플릿을 반환한다

이게 FlowMind의 안정성을 만드는 축이다.

## 11. Dispatcher는 무엇인가

Dispatcher는 전체 시스템의 관제자다.

역할은 단순하다.

1. Context 로드
2. NLU 수행
3. confidence 평가
4. route 결정
5. 결과 반환 및 로그 저장

### Route 종류

- `SCENARIO`
- `LLM`
- `AGENT`

### 왜 Dispatcher가 중요한가

이 프로젝트의 핵심은 "답변 생성"이 아니라 "어느 엔진으로 보낼지 결정"하는 것이다.

그래서 Dispatcher가 사실상 FlowMind의 중심이다.

## 12. LLM은 정확히 무엇을 담당하는가

FlowMind에서 LLM은 모든 것을 처리하지 않는다.

주요 역할은 아래와 같다.

- 애매한 질문의 유연한 해석
- 불만, 감정 표현, 설명 요청 대응
- low-confidence 상황에서의 자연스러운 보완 응답
- 구조화된 fallback 응답 생성

### 맡기지 않는 영역

다음은 기본적으로 규칙 기반이 우선이다.

- 민감한 액션 실행
- 확정된 업무 처리
- 정형 데이터 조회 로직
- 권한 검증

즉, LLM은 "설명과 보완"에 강하고, "확정 실행"은 규칙 기반 엔진이 담당한다.

## 13. Prompt Engineering은 어디에 들어가는가

Prompt Engineering은 단순히 시스템 프롬프트를 쓰는 작업이 아니다.

FlowMind에서는 아래를 포함한다.

- 역할 정의
- 제약 조건 정의
- few-shot example 관리
- 출력 형식 강제
- 버전 관리
- 실험과 비교

### 왜 필요한가

같은 모델이라도 프롬프트가 달라지면 다음이 달라진다.

- 응답 길이
- 톤
- 구조화 정도
- 안정성
- 오답률

그래서 Prompt는 코드 안의 문자열이 아니라 운영 자산으로 분리해야 한다.

## 14. PromptOps는 무엇인가

PromptOps는 프롬프트를 운영 대상으로 관리하는 체계다.

포함되는 작업:

- 버전 생성
- 활성 버전 관리
- A/B 테스트
- 성능 비교
- 승격 / 롤백

### 왜 따로 필요한가

LLM 품질 문제는 종종 코드 문제가 아니라 Prompt 문제다.

이걸 운영적으로 다룰 수 있어야 FlowMind가 단순 데모가 아니라 플랫폼이 된다.

## 15. Analytics는 왜 중요한가

대화 시스템은 "잘 된다"는 느낌만으로 운영하면 금방 한계에 부딪힌다.

FlowMind는 최소한 아래를 숫자로 봐야 한다.

- Intent 정확도
- Scenario 비율
- LLM fallback 비율
- 평균 응답시간
- 사용자 평점
- 미매칭 발화 빈도

이 수치가 있어야 다음 질문에 답할 수 있다.

- 어떤 Intent가 약한가
- 어떤 시나리오에서 이탈이 많은가
- LLM이 너무 많이 호출되고 있는가
- Prompt를 바꾼 뒤 실제로 좋아졌는가

## 16. 예시로 보는 전체 동작

입력:

`카톡으로 요금표 보내줘`

### 1단계. NLP / 전처리

- 문장 정규화
- 조사 제거 또는 형태 분석 보조
- 핵심 표현 인식

### 2단계. NLU

- Intent 후보 추출: `SEND_DOCUMENT`, `BILLING_INQUIRY`
- Entity 추출: `CHANNEL=KAKAOTALK`, `DOCUMENT_TYPE=BILL_TABLE`

### 3단계. Context 결합

- 사용자 채널이 실제로 카카오톡인지 확인
- 세션에 이미 고객 번호가 있는지 확인
- VIP 여부 확인

### 4단계. Dispatcher 판단

- confidence 높음
- 필요한 정보 충분함
- 규칙 기반 처리 가능

결정:

- `SCENARIO` route

### 5단계. Scenario 실행

- 고객 확인
- 발송 처리
- 확인 메시지 반환

### 6단계. Logging

- route 저장
- intent 저장
- latency 저장
- 사용자 평가 저장

## 17. 구현 순서에서 중요한 기준

이 프로젝트는 개념이 많아 보여도 구현 순서는 단순해야 한다.

### 먼저 만들 것

- Intent
- Utterance
- Entity
- Slot
- SessionContext
- Dispatcher
- Scenario 1개

### 나중에 붙일 것

- Prompt versioning
- A/B 테스트
- Data Lab
- 고급 Analytics

즉, 먼저 "정확히 한 줄기의 대화가 끝까지 동작하는 구조"를 만들어야 한다.

## 18. 한 문장으로 다시 정리하면

FlowMind는 사용자의 문장을 `Intent와 Context`로 구조화해서 가능한 것은 규칙 기반으로 처리하고, 어려운 것은 `Prompt가 설계된 LLM`으로 보완하며, 모든 결과를 운영 데이터로 축적하는 하이브리드 대화 플랫폼이다.
