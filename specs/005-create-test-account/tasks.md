---
description: "Task list for creating a test account via API"
---

# Tasks: create-test-account

**Input**: Design documents from `/specs/005-create-test-account/`

**Prerequisites**: plan.md (required), spec.md (required), research.md, data-model.md, contracts/api.md

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1)
- Exact file paths are provided.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

*(No setup tasks needed. Project and database are already initialized)*

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

*(No foundational tasks needed. Global exception handling and Hexagonal architecture repositories are already in place)*

---

## Phase 3: User Story 1 - Create a test account (Priority: P1) 🎯 MVP

**Goal**: As a QA engineer or developer running automated API tests, I need an endpoint to programmatically create test accounts in the database with a specified initial balance and account ID.

**Independent Test**: Can be tested independently by making a POST request to the endpoint and verifying that the account is created in PostgreSQL and Redis.

### Tests for User Story 1

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [X] T001 [P] [US1] Create unit test in `src/test/java/com/corebank/application/command/CreateAccountUseCaseImplTest.java` (mocking the repositories)
- [X] T002 [P] [US1] Create integration test in `src/test/java/com/corebank/infrastructure/web/TestAccountControllerTest.java` (using MockMvc and Testcontainers/MockBeans)

### Implementation for User Story 1

- [X] T003 [P] [US1] Create Use Case interface and records in `src/main/java/com/corebank/application/command/CreateAccountUseCase.java`
- [X] T004 [US1] Implement Use Case in `src/main/java/com/corebank/application/command/CreateAccountUseCaseImpl.java` (Inject `AccountLedgerRepository` and `BalanceRepository`. Save state to both synchronously without triggering RabbitMQ Outbox events)
- [X] T005 [US1] Implement Controller in `src/main/java/com/corebank/infrastructure/web/TestAccountController.java` (Expose `POST /api/v1/test/accounts`, validate initialBalance >= 0, annotate with `@Profile({"dev", "test"})`)

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [X] T006 Run quickstart.md validation

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: N/A
- **Foundational (Phase 2)**: N/A
- **User Stories (Phase 3+)**: US1 is independent.
- **Polish (Final Phase)**: Depends on US1 completion.

### Within Each User Story

- Tests MUST be written and FAIL before implementation
- Interface before Implementation
- Use Case implementation before Controller implementation
- Core implementation before testing via Quickstart

### Parallel Opportunities

- Tests (T001, T002) and Interface (T003) can be worked on in parallel.

---

## Parallel Example: User Story 1

```bash
# Launch interface and tests together:
Task: "Create Use Case interface..."
Task: "Create unit test..."
Task: "Create integration test..."
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 3: User Story 1
2. **STOP and VALIDATE**: Test User Story 1 independently using `quickstart.md`
3. MVP is ready for dev/test environments.

---

## Phase 5: Convergence

- [X] T007 CRITICAL Add @Valid to TestAccountController endpoint and Bean Validation annotations (@NotNull, @PositiveOrZero) to CreateTestAccountRequest DTO per Constitution X (contradicts)
- [X] T008 [P] Add timeout assertion (< 200ms) to TestAccountControllerTest per SC-001 (partial)


---

## Phase 6: Convergence

- [X] T009 CRITICAL Add OpenAPI/Swagger annotations (@Operation, @ApiResponses, @Tag, @Schema) to TestAccountController per Constitution VIII (contradicts)

