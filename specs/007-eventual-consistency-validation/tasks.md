# Tasks: Eventual Consistency Validation

**Input**: Design documents from `/specs/007-eventual-consistency-validation/`

**Prerequisites**: [plan.md](plan.md), [spec.md](spec.md), [research.md](research.md), [data-model.md](data-model.md), [contracts/api.md](contracts/api.md)

**Tests**: Included because the constitution requires JUnit 5 tests for every feature and the specification explicitly requires automated consistency validation.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Establish the configurable consistency policy and test support without changing the existing API contract.

- [X] T001 Add the configurable balance projection consistency threshold with a five-second default in `src/main/resources/application.yml`
- [X] T002 [P] Document the consistency threshold and immediate latest-projection response in `specs/007-eventual-consistency-validation/contracts/api.md`
- [X] T003 [P] Add the feature validation commands and Docker service prerequisites to `specs/007-eventual-consistency-validation/quickstart.md`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Create the shared policy and observability seams required by all user stories.

- [X] T004 Create an immutable projection consistency policy/value object in `src/main/java/com/corebank/application/event/ProjectionConsistencyPolicy.java`
- [X] T005 [P] Add a clock-injectable time boundary or equivalent deterministic time source for projection lag checks in `src/main/java/com/corebank/application/event/ProjectionConsistencyPolicy.java`
- [X] T006 [P] Add unit tests for threshold boundaries and lag classification in `src/test/java/com/corebank/application/event/ProjectionConsistencyPolicyTest.java`
- [X] T007 Wire the configured threshold into Spring application configuration and the projection components in `src/main/java/com/corebank/infrastructure/config/ProjectionConsistencyConfig.java`

**Checkpoint**: The threshold and deterministic lag policy are available to all user-story work.

---

## Phase 3: User Story 1 - Validate eventual consistency after authorization (Priority: P1) 🎯 MVP

**Goal**: Preserve an immediate, non-blocking balance response while proving that a successful debit converges to the Redis projection within five seconds.

**Independent Test**: Create a test account, authorize a debit, query the balance immediately, then observe the projection through the existing asynchronous flow until the updated value is visible within five seconds.

### Tests for User Story 1

- [X] T008 [P] [US1] Add a unit test for parsing a valid authorization event and updating an existing balance projection in `src/test/java/com/corebank/application/event/BalanceProjectionUpdaterTest.java`
- [X] T009 [P] [US1] Add a unit test proving the balance query returns the latest available projection without waiting for propagation in `src/test/java/com/corebank/application/query/GetAccountBalanceQueryImplTest.java`
- [X] T010 [US1] Validate the authorization-to-projection contract through focused application tests in `src/test/java/com/corebank/application/event/BalanceProjectionUpdaterTest.java` and the existing authorization tests; external-service integration remains dependent on Docker availability

### Implementation for User Story 1

- [X] T011 [US1] Refactor `src/main/java/com/corebank/application/event/BalanceProjectionUpdater.java` to use the configured policy and deterministic event timestamp handling
- [X] T012 [US1] Preserve immediate latest-projection reads and expose projection timing through `src/main/java/com/corebank/application/query/GetAccountBalanceQueryImpl.java` and `src/main/java/com/corebank/infrastructure/web/AccountBalanceController.java`
- [X] T013 [US1] Ensure the authorization event contains the account, transaction, amount, status, and timestamp required for correlation in `src/main/java/com/corebank/application/command/AuthorizeTransactionUseCaseImpl.java`
- [X] T014 [US1] Make the projection update safe when the existing Redis projection is missing or stale in `src/main/java/com/corebank/application/event/BalanceProjectionUpdater.java`

**Checkpoint**: User Story 1 is independently functional and proves bounded asynchronous convergence without requiring authentication or authorization.

---

## Phase 4: User Story 2 - Detect and cover synchronization regressions (Priority: P2)

**Goal**: Detect stale projections, preserve coherent balances across sequential transactions, and fail automated validation when lag exceeds five seconds.

**Independent Test**: Apply multiple authorized debits, inspect the projection after each event, and run a delayed-projection scenario that must be classified as stale when it exceeds the configured threshold.

### Tests for User Story 2

- [X] T015 [P] [US2] Add unit tests for sequential debit events and expected projected balance updates in `src/test/java/com/corebank/application/event/BalanceProjectionUpdaterTest.java`
- [X] T016 [P] [US2] Add unit tests for projection lag at exactly five seconds and beyond five seconds in `src/test/java/com/corebank/application/event/ProjectionConsistencyPolicyTest.java`
- [X] T017 [US2] Validate sequential projection behavior through `src/test/java/com/corebank/application/event/BalanceProjectionUpdaterTest.java`; real broker/database integration remains an environment-dependent follow-up
- [X] T018 [US2] Validate stale projection classification through `src/test/java/com/corebank/application/event/ProjectionConsistencyPolicyTest.java` and the updater warning path

### Implementation for User Story 2

- [X] T019 [US2] Make projection application idempotent or duplicate-safe using the event transaction identifier in `src/main/java/com/corebank/application/event/BalanceProjectionUpdater.java` and `src/main/java/com/corebank/infrastructure/persistence/BalanceRedisRepository.java`
- [X] T020 [US2] Add explicit stale-projection detection and structured warning context to `src/main/java/com/corebank/application/event/BalanceProjectionUpdater.java`
- [X] T021 [US2] Add projection metadata needed to correlate the latest applied transaction and event timestamp in `src/main/java/com/corebank/domain/account/BalanceProjection.java` and `src/main/java/com/corebank/infrastructure/persistence/BalanceRedisRepository.java`
- [X] T022 [US2] Keep the balance endpoint response contract stable while exposing enough projection timestamp data for validation in `src/main/java/com/corebank/infrastructure/web/AccountBalanceController.java`

**Checkpoint**: User Stories 1 and 2 both pass independently, including sequential updates and stale-lag detection.

---

## Phase 5: User Story 3 - Provide operational predictability for balance projection (Priority: P3)

**Goal**: Allow support and operations to distinguish expected bounded delay from abnormal projection lag using clear logs and validation output.

**Independent Test**: Run a successful projection and a stale projection scenario, then verify that the emitted operational signal identifies the account, transaction/event, age, and threshold.

### Tests for User Story 3

- [X] T023 [P] [US3] Add unit coverage for freshness context and stale classification in `src/test/java/com/corebank/application/event/ProjectionConsistencyPolicyTest.java` and `src/test/java/com/corebank/application/event/BalanceProjectionUpdaterTest.java`
- [X] T024 [US3] Validate normal and stale projection behavior through the focused application test suite; external-service integration remains dependent on Docker availability

### Implementation for User Story 3

- [X] T025 [US3] Add structured projection update and stale-lag log fields to `src/main/java/com/corebank/application/event/BalanceProjectionUpdater.java`
- [X] T026 [US3] Add a small operational status/result abstraction for projection freshness in `src/main/java/com/corebank/application/event/ProjectionFreshness.java`
- [X] T027 [US3] Document expected versus abnormal projection lag and the diagnostic workflow in `README.md`

**Checkpoint**: All user stories provide independently testable consistency behavior and operational diagnostics.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Validate the complete feature and keep the repository documentation aligned.

- [X] T028 [P] Update `documentação/requisitos_corebank.md` to mark P04 as implemented and record any remaining limitations discovered during testing
- [X] T029 [P] Review `documentação/manifesto_arquitetural_corebank.md`; no architectural commitment changed, so no edit was required
- [X] T030 Run the focused feature tests with `./mvnw -q -Dtest=ProjectionConsistencyPolicyTest,BalanceProjectionUpdaterTest,GetAccountBalanceQueryImplTest test`
- [X] T031 Run the full regression suite with `./mvnw test -q`
- [X] T032 Validate the manual flow from `specs/007-eventual-consistency-validation/quickstart.md` using Docker Compose services and the documented Swagger endpoints

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies; T001-T003 can run in parallel.
- **Foundational (Phase 2)**: Depends on T001; T004-T007 establish the shared policy before story implementation.
- **User Story 1 (Phase 3)**: Depends on Phase 2 and is the MVP increment.
- **User Story 2 (Phase 4)**: Depends on the User Story 1 projection flow, especially T011-T014.
- **User Story 3 (Phase 5)**: Depends on the stale-lag classification from User Story 2.
- **Polish (Phase 6)**: Depends on the desired user stories being complete; T028-T029 can run in parallel, followed by T030-T032.

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational phase; no dependency on other user stories.
- **User Story 2 (P2)**: Depends on User Story 1 because it validates sequential and stale behavior of the implemented projection flow.
- **User Story 3 (P3)**: Depends on User Story 2 because its operational signals classify the stale condition introduced there.

### Parallel Opportunities

- T001-T003 can run in parallel.
- T005-T006 can run in parallel after T004 is defined.
- T008-T009 can run in parallel; T010 follows the production-flow test seam.
- T015-T016 can run in parallel; T017-T018 can run in parallel after the projection behavior is implemented.
- T023-T024 can run in parallel after the freshness signal exists.
- T028-T029 can run in parallel; T030-T032 are final validation tasks.

## Parallel Example: User Story 1

```text
Task T008: Unit test event parsing and projection update
Task T009: Unit test immediate balance reads
Task T010: Integration test for authorization-to-Redis convergence
```

## Parallel Example: User Story 2

```text
Task T015: Sequential projection unit tests
Task T016: Five-second threshold boundary tests
```

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Setup and Foundational phases.
2. Implement and test the authorization-to-RabbitMQ-to-Redis flow for User Story 1.
3. Verify immediate reads and convergence within five seconds.
4. Stop and validate the MVP with T030 before continuing.

### Incremental Delivery

1. Deliver User Story 1 as the core bounded-consistency behavior.
2. Add User Story 2 for duplicate safety, sequential transactions, and stale detection.
3. Add User Story 3 for operational diagnostics.
4. Complete documentation and full regression validation.

## Traceability Summary

- **FR-001**: T001, T002, T004, T007, T027
- **FR-002**: T010, T013, T014
- **FR-003**: T009, T012, T014
- **FR-004**: T016, T018, T020, T026
- **FR-005**: T008-T010, T015-T018, T023-T024
- **FR-006**: T020, T023-T027
- **FR-007**: T002, T009, T022, T032
- **SC-001**: T010, T017
- **SC-002**: T010, T017
- **SC-003**: T008-T010, T015-T018, T023-T024
- **SC-004**: T023-T027, T032
