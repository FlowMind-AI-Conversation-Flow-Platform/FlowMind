# ConvoForge Hybrid Strategy

## Objective

ConvoForge should optimize both deterministic automation and generative flexibility in one platform.

- `Intent layer`: rule-based, measurable, operationally safe
- `LLM layer`: flexible fallback for ambiguity, complaints, and unexpected phrasing

The platform should treat prompt engineering and context engineering as separate concerns with a shared dispatcher.

## System Model

### 1. Context Engineering

Context is not a single object. It should be modeled in layers:

- `UserContext`
  - channel
  - language
  - account age
  - tier
  - last interaction
- `SessionContext`
  - current intent
  - conversation history
  - collected slots
  - last updated time
- `DomainContext`
  - domain
  - category
  - entity library
  - common intents

This separation keeps long-lived profile data, short-lived conversation state, and domain-specific rules independent.

### 2. Prompt Engineering

Prompting should be treated as an operational asset, not embedded string literals.

- template name + version
- role / policy / constraints
- examples
- output contract
- performance metrics
- traffic split
- active state

This allows prompt versioning, controlled rollout, and rollback.

### 3. Hybrid Dispatcher

The dispatcher is the control plane.

```text
User Input
  -> load user/session/domain context
  -> classify with context
  -> confidence threshold check
     -> high confidence: scenario executor
     -> low confidence: LLM fallback with prompt augmentation
  -> log route, response, timing, and rating
```

## Recommended Service Boundaries

### Context Services

- `UserContextService`
- `SessionContextService`
- `DomainContextService`
- `ConversationLogService`

### Decisioning Services

- `ContextAwareIntentClassifier`
- `EntityExtractor`
- `DynamicSlotFiller`
- `ScenarioExecutor`
- `HybridDispatcher`

### PromptOps Services

- `PromptTemplateService`
- `PromptSelectionService`
- `PromptEvaluationService`
- `PromptPromotionJob`

### LLM Integration

- `PromptAugmentationService`
- `LlmGateway`
- `ResponseGuardService`

## Routing Rules

Use confidence-based routing, but do not rely on a single numeric threshold alone.

### Route to Scenario When

- intent confidence is high
- required slots are complete or can be clarified deterministically
- operation is sensitive and should stay rule-bound
- business policy requires predictable execution

### Route to LLM When

- intent is ambiguous
- user sentiment or complaint needs flexible language
- the utterance references prior context loosely
- no safe deterministic scenario exists

### Additional Guardrails

- never expose raw PII in prompts
- mask slot values before prompt injection
- include only relevant conversation history
- constrain output shape for downstream automation

## Data Model

### Core Tables

- `user_context`
- `session_context`
- `prompt_template`
- `conversation_log`

### Important Additions

The original proposal is directionally correct, but these fields are worth adding early:

- `fallback_reason`
- `model_name`
- `prompt_version`
- `latency_ms`
- `token_usage`
- `masked_entities`
- `resolution_status`

These fields matter for prompt tuning and cost analysis.

## Implementation Sequence

### Phase 1. Deterministic Foundation

- define intent catalog
- define slot schema per intent
- implement session persistence
- implement context-aware classifier
- implement slot carry-over

### Phase 2. LLM Fallback

- define prompt template schema
- implement active prompt lookup
- build prompt augmentation from masked context
- add structured LLM response contract

### Phase 3. Analytics and Optimization

- persist route and confidence
- track fallback reasons
- add per-version prompt metrics
- add A/B prompt assignment
- add promotion policy with minimum sample threshold

## Design Corrections To Apply

Some parts of the draft should be tightened before implementation:

### 1. Auto-promotion needs safeguards

Hourly promotion to the highest average rating is too naive. Use:

- minimum sample size
- confidence interval or statistical significance check
- holdout or canary traffic
- manual approval for regulated flows

### 2. Conversation history should be summarized

Full history injection will scale poorly. Store both:

- raw event log
- rolling summary for prompt augmentation

### 3. Continuation detection needs better signals

`cosineSimilarity(currentInput, previousIntent)` is not sufficient. Prefer:

- previous intent
- previous action
- unresolved required slots
- temporal expressions
- channel-specific phrasing rules

### 4. Prompt output should separate user text from machine state

A better contract is:

```json
{
  "message": "고객에게 보여줄 답변",
  "action": "WAIT_FOR_INPUT",
  "next_step": "CLARIFY",
  "confidence": "HIGH",
  "reason": "missing_customer_id"
}
```

This keeps automation stable and easier to validate.

## MVP Success Metrics

- intent classification accuracy
- slot completion rate
- scenario routing ratio
- LLM fallback ratio
- average user rating
- escalation rate
- average response latency

## Recommended Next Build

If this repository is the starting point, the next concrete implementation should create:

1. domain models for user, session, prompt template, and conversation log
2. persistence adapters for database and Redis
3. a first-pass `HybridDispatcher`
4. one end-to-end flow such as billing inquiry with slot filling
5. one controlled LLM fallback path with masked context
