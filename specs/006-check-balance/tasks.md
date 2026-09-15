---
description: "Task list for Check Balance feature"
---

# Tasks: check-balance

**Input**: Design documents from `/specs/006-check-balance/`

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

## Phase 3: User Story 1 - Consulta Rápida de Saldo (Priority: P1) 🎯 MVP

**Goal**: Como um cliente final ou sistema interfaceador, eu desejo consultar o saldo atualizado de uma conta bancária para visualizá-lo instantaneamente na tela.

**Independent Test**: Pode ser testado através da interface de API consultando uma conta específica e verificando se o saldo retornado reflete as operações anteriores processadas.

### Tests for User Story 1

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [x] T001 [P] [US1] Create unit test for the Use Case in `src/test/java/com/corebank/application/query/GetAccountBalanceQueryImplTest.java` (mocking `BalanceRepository`)
- [x] T002 [P] [US1] Create integration test for Controller in `src/test/java/com/corebank/infrastructure/web/AccountBalanceControllerTest.java` using MockMvc (with 200, 400, 404 scenarios and timeout <50ms assertion per SC-001)

### Implementation for User Story 1

- [x] T003 [P] [US1] Create Use Case interface and records in `src/main/java/com/corebank/application/query/GetAccountBalanceQuery.java`
- [x] T004 [US1] Implement Use Case in `src/main/java/com/corebank/application/query/GetAccountBalanceQueryImpl.java` (Inject `BalanceRepository` and retrieve the balance projection)
- [x] T005 [US1] Implement Controller in `src/main/java/com/corebank/infrastructure/web/AccountBalanceController.java` (Expose `GET /api/v1/accounts/{accountId}/balance`, add OpenAPI annotations like `@Operation`, `@ApiResponses`, `@Tag` per Constitution VIII, call Use Case, handle 404 if missing)

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect the balance-read user story and ensure contract clarity

- [x] T006 Run quickstart.md validation to manually verify the full flow (200 OK, 404 Not Found, 400 Bad Request)
- [x] T007 [US1] Consolidate the duplicated balance query port pair into a single IN port and refactor the controller to use the canonical query contract, preserving the Hexagonal Architecture and avoiding duplicate entry points.
- [x] T008 [US1] Validate the stale-read contract for eventual consistency: when the Redis projection has not caught up, the balance endpoint must return 404 and the API contract must document the retry window of up to 5 seconds.
- [x] T009 [US1] Verify the OpenAPI contract for GET /api/v1/accounts/{accountId}/balance, including 200, 400 and 404 response examples and validation messages.

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

**Purpose**: Close remaining gaps between spec/plan intent and current implementation

- [x] T007 [US1] Remove duplicate query port pair (`GetBalanceQuery.java` and `GetBalanceQueryImpl.java` in `src/main/java/com/corebank/application/query/`) and refactor `AccountController` in `src/main/java/com/corebank/infrastructure/web/AccountController.java` to use `GetAccountBalanceQuery` instead, consolidating the balance query into a single IN port per Constitution IX (contradicts)
- [x] T008 [US1] Verify and ensure the global exception handler returns a structured 400 error body (with message) for `MethodArgumentTypeMismatchException` on invalid UUID path variables in the balance endpoint, per FR-004 and edge case "identificador malformado" (partial)
