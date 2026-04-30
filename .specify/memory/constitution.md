# FlowMind Constitution

## Core Principles

### I. Hybrid-First Decision Policy
FlowMind MUST process requests with deterministic intent/slot flow first, then use LLM fallback only when confidence is low, request is composite, or sentiment-heavy escalation is detected.

### II. Traceability by Default
All dispatch decisions MUST persist trace fields (intent score, fallback reason, sessionId, requestId, latency) so outcomes are auditable and improvable.

### III. Test and Gate Discipline (Non-Negotiable)
No implementation is complete without passing quality gates. Backend changes MUST pass `spotlessCheck` and `test`. Docs/planning changes MUST pass strict-gate lint (link integrity and status consistency).

### IV. Contract-First API Evolution
External API behavior MUST be explicit and backward-safe: error envelope fields, status semantics, and validation rules are documented before release and covered by controller tests.

### V. Human-AI Handoff Integrity
Every issue/PR MUST include three handoff sections: changed files, verification results, and residual risks. Automation should fill these sections by default and humans refine only when needed.

## Technical and Operational Constraints

- Primary backend stack: Java 21, Spring Boot, Gradle.
- Conversation domain for current phase: Finance VoiceOps (identity check, account info, transaction history, card lost/limit).
- Local-first cost policy: prefer free local inference/runtime when practical.
- Security/privacy baseline: never log raw sensitive identifiers beyond allowed masked fields.

## Workflow and Quality Gates

- Plan-first workflow:
  - define spec
  - derive plan
  - break into tasks
  - implement with verification
- Branch strategy:
  - `dev` baseline
  - feature branches for isolated changes
- Mandatory checks:
  - backend: `backend\gradlew.bat spotlessCheck test`
  - orchestrator policy checks: `node scripts/orchestrator-manager.spec.mjs`

## Governance

- This constitution supersedes local habits and ad-hoc instructions when conflicts occur.
- Amendments require:
  - reason for change
  - affected workflows
  - migration impact note
- Compliance is validated during PR review and strict-gate execution.

**Version**: 1.0.0 | **Ratified**: 2026-04-30 | **Last Amended**: 2026-04-30
