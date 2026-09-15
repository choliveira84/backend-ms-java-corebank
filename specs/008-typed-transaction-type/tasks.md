# Tasks: Typed Transaction Type

**Input**: Design documents from `/specs/008-typed-transaction-type/`

**Prerequisites**: [plan.md](plan.md), [spec.md](spec.md), [research.md](research.md), [data-model.md](data-model.md), [contracts/api.md](contracts/api.md)

**Tests**: Included because the project constitution requires JUnit 5 tests for every feature and the specification defines explicit accepted and rejected input scenarios.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Establish the shared transaction vocabulary and preserve the existing API/error contract.

- [X] T001 Create the closed `TransactionType` enum with `DEBIT`, `TRANSFER`, and `PIX` in `src/main/java/com/corebank/domain/transaction/TransactionType.java`
- [X] T002 [P] Document the supported transaction type values and strict case-sensitive behavior in `README.md`
- [X] T003 [P] Update the transaction authorization example and supported values in `specs/008-typed-transaction-type/contracts/api.md`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Prepare the application command and serialization boundaries for the typed value.

- [X] T004 Change `AuthorizeTransactionCommand.type` from `String` to `TransactionType` in `src/main/java/com/corebank/application/command/AuthorizeTransactionUseCase.java`
- [X] T005 [P] Add enum-focused unit coverage for all supported values in `src/test/java/com/corebank/domain/transaction/TransactionTypeTest.java`
- [X] T006 [P] Verify the existing `GlobalExceptionHandler` maps enum binding and missing/null request errors to structured HTTP 400 responses in `src/main/java/com/corebank/infrastructure/web/exception/GlobalExceptionHandler.java`
- [X] T007 Update the transaction history and outbox serialization boundary to persist enum names without schema changes in `src/main/java/com/corebank/application/command/AuthorizeTransactionUseCaseImpl.java`

**Checkpoint**: The application has one typed transaction vocabulary and can preserve the existing text-based persistence/event formats.

---

## Phase 3: User Story 1 - Accept only supported transaction types (Priority: P1) MVP

**Goal**: Accept exactly `DEBIT`, `TRANSFER`, and `PIX` at the authorization endpoint while rejecting unknown values before business execution.

**Independent Test**: Submit MockMvc requests for all three supported values and an unsupported value, then verify accepted requests reach the mocked use case and invalid requests return 400 without invoking it.

### Tests for User Story 1

- [X] T008 [P] [US1] Add MockMvc coverage for valid `DEBIT`, `TRANSFER`, and `PIX` request bodies in `src/test/java/com/corebank/infrastructure/web/TransactionControllerTest.java`
- [X] T009 [P] [US1] Add MockMvc coverage for unknown `PIX2` or `CASH` values returning 400 and never invoking the use case in `src/test/java/com/corebank/infrastructure/web/TransactionControllerTest.java`
- [X] T010 [P] [US1] Add MockMvc coverage proving valid enum values are propagated to `AuthorizeTransactionCommand` in `src/test/java/com/corebank/infrastructure/web/TransactionControllerTest.java`

### Implementation for User Story 1

- [X] T011 [US1] Change `TransactionController.TransactionRequest.type` from `String` to `TransactionType` and keep request-body validation in `src/main/java/com/corebank/infrastructure/web/TransactionController.java`
- [X] T012 [US1] Pass the typed `TransactionType` directly into `AuthorizeTransactionCommand` in `src/main/java/com/corebank/infrastructure/web/TransactionController.java`
- [X] T013 [US1] Preserve successful authorization and insufficient-funds behavior for the typed command in `src/main/java/com/corebank/application/command/AuthorizeTransactionUseCaseImpl.java`

**Checkpoint**: User Story 1 is independently functional; invalid transaction types fail at the API boundary and valid values preserve authorization behavior.

---

## Phase 4: User Story 2 - Return clear validation errors (Priority: P2)

**Goal**: Return structured 400 responses for missing, null, blank, whitespace-padded, and differently capitalized transaction types.

**Independent Test**: Submit each invalid representation through MockMvc and verify HTTP 400, structured error content, and zero use-case invocation.

### Tests for User Story 2

- [X] T014 [P] [US2] Add tests for omitted and null `type` values returning structured 400 responses in `src/test/java/com/corebank/infrastructure/web/TransactionControllerTest.java`
- [X] T015 [P] [US2] Add tests for empty, whitespace-padded, lowercase, and mixed-case values returning 400 in `src/test/java/com/corebank/infrastructure/web/TransactionControllerTest.java`
- [X] T016 [US2] Add a regression assertion that invalid type requests do not invoke `AuthorizeTransactionUseCase` in `src/test/java/com/corebank/infrastructure/web/TransactionControllerTest.java`

### Implementation for User Story 2

- [X] T017 [US2] Ensure enum conversion failures identify the invalid request as a 400 ProblemDetail without changing existing error response fields in `src/main/java/com/corebank/infrastructure/web/exception/GlobalExceptionHandler.java`
- [X] T018 [US2] Add OpenAPI schema annotations/examples that enumerate `DEBIT`, `TRANSFER`, and `PIX` for the request type in `src/main/java/com/corebank/infrastructure/web/TransactionController.java`
- [X] T019 [US2] Keep exact case-sensitive binding without trimming or custom normalization in `src/main/java/com/corebank/infrastructure/web/TransactionController.java`

**Checkpoint**: User Stories 1 and 2 both pass independently, with clear boundary errors and no business execution for invalid input.

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Validate downstream compatibility, documentation, and the complete regression suite.

- [X] T020 [P] Add authorization use-case tests for `DEBIT`, `TRANSFER`, and `PIX` command values and preserve existing business assertions in `src/test/java/com/corebank/application/command/AuthorizeTransactionUseCaseTest.java`
- [X] T021 [P] Add assertions that transaction history and outbox payloads preserve the enum name in `src/test/java/com/corebank/application/command/AuthorizeTransactionUseCaseTest.java`
- [X] T022 [P] Update the feature quickstart with valid `PIX` and invalid-value examples in `specs/008-typed-transaction-type/quickstart.md`
- [X] T023 [P] Update the formal API contract and data model if implementation details require clarification in `specs/008-typed-transaction-type/contracts/api.md` and `specs/008-typed-transaction-type/data-model.md`
- [X] T024 Run focused tests with `./mvnw -q -Dtest=TransactionControllerTest,AuthorizeTransactionUseCaseTest,TransactionTypeTest test`
- [X] T025 Run the full regression suite with `./mvnw test -q`
- [X] T026 Verify the Swagger request schema exposes exactly `DEBIT`, `TRANSFER`, and `PIX` using the running application and `specs/008-typed-transaction-type/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: T001 is the base vocabulary; T002-T003 can run in parallel with it.
- **Foundational (Phase 2)**: Depends on T001; T004 and T007 update downstream boundaries, while T005-T006 can run in parallel.
- **User Story 1 (Phase 3)**: Depends on Phase 2; this is the MVP increment.
- **User Story 2 (Phase 4)**: Depends on the controller binding from User Story 1, especially T011-T012.
- **Polish (Phase 5)**: Depends on both user stories; T020-T023 can run in parallel before T024-T026.

### User Story Dependencies

- **User Story 1 (P1)**: Can start after the foundational phase; no dependency on User Story 2.
- **User Story 2 (P2)**: Depends on User Story 1's typed controller binding and validates its error behavior.

### Parallel Opportunities

- T002-T003 can run in parallel.
- T005-T006 can run in parallel after T001.
- T008-T010 can run in parallel as independent controller scenarios.
- T014-T016 can run in parallel as independent invalid-input scenarios.
- T020-T023 can run in parallel before final validation.

## Parallel Example: User Story 1

```text
Task T008: Valid DEBIT, TRANSFER, and PIX MockMvc scenarios
Task T009: Unknown enum value MockMvc scenario
Task T010: Typed command propagation assertion
```

## Implementation Strategy

### MVP First

1. Complete T001, T004, T007, and the User Story 1 tests/implementation.
2. Validate that `DEBIT`, `TRANSFER`, and `PIX` reach the existing authorization flow.
3. Validate that an unknown value is rejected before the use case executes.
4. Run T024 before continuing to the error/documentation polish phase.

### Incremental Delivery

1. Deliver User Story 1 as the typed input boundary.
2. Add User Story 2 for complete invalid-input coverage and structured errors.
3. Complete OpenAPI, README, quickstart, downstream serialization, and full regression validation.

## Traceability Summary

- **FR-001**: T001, T008, T011
- **FR-002**: T001, T004, T011
- **FR-003**: T014, T015, T019
- **FR-004**: T006, T009, T014, T015, T017
- **FR-005**: T009, T016
- **FR-006**: T013, T020, T024, T025
- **FR-007**: T003, T018, T022, T026
- **FR-008**: T001, T003, T018, T023
- **SC-001**: T008, T010, T020
- **SC-002**: T009, T014, T015, T016
- **SC-003**: T003, T018, T026
- **SC-004**: T013, T020, T024, T025
