---
description: "Task list for transaction-authorization feature implementation"
---

# Tasks: transaction-authorization

**Input**: Design documents from `/specs/002-transaction-authorization/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/, quickstart.md

**Tests**: Included as explicitly requested in the plan (JUnit 5 + Mockito).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- Java project: `src/main/java/...`, `src/test/java/...`
- SQL Migrations: `src/main/resources/db/migration/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Verify project skeleton is complete (Already handled in Phase 1-5)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T002 [P] Create Flyway migration `V1__init_schema.sql` in `src/main/resources/db/migration/V1__init_schema.sql` (tables: `account_ledger`, `transaction_history`, `outbox_events`)
- [x] T003 [P] Configure Redis properties and `RedisTemplate` in `src/main/java/com/corebank/infrastructure/config/RedisConfig.java`
- [x] T004 [P] Configure RabbitMQ exchange and queues in `src/main/java/com/corebank/infrastructure/config/RabbitMQConfig.java`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Debit Transaction Authorization (Priority: P1) 🎯 MVP

**Goal**: Authorize a debit transaction checking balance (ACID) and persisting in outbox.

**Independent Test**: POST a transaction and verify the database `account_ledger` is updated and `outbox_events` is inserted.

### Tests for User Story 1 (OPTIONAL - only if tests requested) ⚠️

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [x] T005 [P] [US1] Create unit tests for `AuthorizeTransactionUseCase` in `src/test/java/com/corebank/application/command/AuthorizeTransactionUseCaseTest.java`
- [x] T006 [P] [US1] Create integration test for `TransactionController` in `src/test/java/com/corebank/infrastructure/web/TransactionControllerTest.java`

### Implementation for User Story 1

- [x] T007 [P] [US1] Create `AccountLedger` entity (JPA) in `src/main/java/com/corebank/domain/account/AccountLedger.java`
- [x] T008 [P] [US1] Create `TransactionHistory` entity (JPA) in `src/main/java/com/corebank/domain/transaction/TransactionHistory.java`
- [x] T009 [P] [US1] Create `OutboxEvent` entity (JPA) in `src/main/java/com/corebank/domain/transaction/OutboxEvent.java`
- [x] T010 [US1] Create `AccountLedgerRepository`, `TransactionHistoryRepository`, `OutboxEventRepository` interfaces in `src/main/java/com/corebank/infrastructure/persistence/`
- [x] T011 [US1] Implement `AuthorizeTransactionUseCase` with `@Transactional` in `src/main/java/com/corebank/application/command/AuthorizeTransactionUseCase.java`
- [x] T012 [US1] Implement `TransactionController` `POST /api/v1/transactions/authorize` in `src/main/java/com/corebank/infrastructure/web/TransactionController.java`

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - High-Speed Balance Inquiry (Priority: P1)

**Goal**: Return fast balance from Redis.

**Independent Test**: Query the balance endpoint and verify it returns a response under 50ms from Redis.

### Tests for User Story 2 (OPTIONAL - only if tests requested) ⚠️

- [x] T013 [P] [US2] Create unit tests for `GetBalanceQuery` in `src/test/java/com/corebank/application/query/GetBalanceQueryTest.java`
- [x] T014 [P] [US2] Create integration test for balance endpoint in `src/test/java/com/corebank/infrastructure/web/AccountControllerTest.java`

### Implementation for User Story 2

- [x] T015 [P] [US2] Create `BalanceProjection` record in `src/main/java/com/corebank/domain/account/BalanceProjection.java`
- [x] T016 [US2] Implement `BalanceRedisRepository` in `src/main/java/com/corebank/infrastructure/persistence/BalanceRedisRepository.java`
- [x] T017 [US2] Implement `GetBalanceQuery` in `src/main/java/com/corebank/application/query/GetBalanceQuery.java`
- [x] T018 [US2] Implement `GET /api/v1/accounts/balance` in `src/main/java/com/corebank/infrastructure/web/AccountController.java`

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - Domain Event Generation (Priority: P2)

**Goal**: Publish Outbox events to RabbitMQ and update Redis Projection (Eventual Consistency).

**Independent Test**: Execute a successful transaction and verify that a `TransactionAuthorizedEvent` is emitted to the message broker, and the Redis balance is updated within 5s.

### Tests for User Story 3 (OPTIONAL - only if tests requested) ⚠️

- [x] T019 [P] [US3] Create tests for `OutboxEventPublisher` in `src/test/java/com/corebank/application/event/OutboxEventPublisherTest.java`
- [x] T020 [P] [US3] Create tests for `BalanceProjectionUpdater` in `src/test/java/com/corebank/application/event/BalanceProjectionUpdaterTest.java`

### Implementation for User Story 3

- [x] T021 [US3] Create `OutboxEventPublisher` scheduled task in `src/main/java/com/corebank/application/event/OutboxEventPublisher.java`
- [x] T022 [US3] Create `BalanceProjectionUpdater` (@RabbitListener) in `src/main/java/com/corebank/application/event/BalanceProjectionUpdater.java`

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [x] T023 Run quickstart.md validation script
- [x] T024 Check checkstyle and PMD rules

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 3 (P2)**: Can start after Foundational (Phase 2) - Integrates with US1 and US2.

### Within Each User Story

- Tests (if included) MUST be written and FAIL before implementation
- Models before services
- Services before endpoints
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, all user stories can start in parallel (if team capacity allows)
- All tests for a user story marked [P] can run in parallel
- Models within a story marked [P] can run in parallel

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together:
Task: "Create unit tests for AuthorizeTransactionUseCase"
Task: "Create integration test for TransactionController"

# Launch all models for User Story 1 together:
Task: "Create AccountLedger entity"
Task: "Create TransactionHistory entity"
Task: "Create OutboxEvent entity"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Add User Story 3 → Test independently → Deploy/Demo
5. Each story adds value without breaking previous stories
