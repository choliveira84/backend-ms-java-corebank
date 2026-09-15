---
description: "Task list for Global Exception Handling implementation"
---

# Tasks: Global Exception Handling

**Input**: Design documents from `/specs/004-global-exception-handling/`

**Prerequisites**: plan.md, spec.md, data-model.md, contracts/api.md, quickstart.md

**Tests**: Required as per Constitution Principle VI (Unit Testing MUST always be implemented).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Exact file paths are included in descriptions

## Path Conventions

- Java source code: `src/main/java/com/corebank/`
- Java test code: `src/test/java/com/corebank/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

*(No setup tasks required as `spring-webmvc` is already present and configured in the project).*

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

- [x] T001 Create `DomainException.java` extending `RuntimeException` in `src/main/java/com/corebank/domain/exception/DomainException.java`.

**Checkpoint**: Foundation ready - user story implementation can now begin.

---

## Phase 3: User Story 1 - Standardized API Error Responses (Priority: P1) 🎯 MVP

**Goal**: Provide a predictable and standardized format (RFC 7807 ProblemDetail) for all API errors.

**Independent Test**: Can be tested by triggering a 500 error or validation error and checking if the response matches the `ProblemDetail` schema.

### Tests for User Story 1

- [x] T002 [P] [US1] Create unit tests for GlobalExceptionHandler in `src/test/java/com/corebank/infrastructure/web/exception/GlobalExceptionHandlerTest.java` verifying 500 and 400 mappings.

### Implementation for User Story 1

- [x] T003 [US1] Create `GlobalExceptionHandler.java` annotated with `@ControllerAdvice` in `src/main/java/com/corebank/infrastructure/web/exception/GlobalExceptionHandler.java`.
- [x] T004 [US1] Implement `@ExceptionHandler(Exception.class)` in `GlobalExceptionHandler` to return `ProblemDetail` with 500 status.
- [x] T005 [US1] Implement `@ExceptionHandler(MethodArgumentNotValidException.class)` in `GlobalExceptionHandler` to return `ProblemDetail` with 400 status and invalid_params details.

**Checkpoint**: At this point, User Story 1 should be fully functional. Any generic exception or payload validation error will return a standard RFC 7807 response.

---

## Phase 4: User Story 2 - Custom Business Exceptions (Priority: P1)

**Goal**: Domain-specific custom exceptions mapped to correct HTTP status codes to clean up controllers and use-cases.

**Independent Test**: Can be tested by executing the `curl` scenarios in `quickstart.md` (e.g. 404 for missing account, 422 for insufficient funds).

### Tests for User Story 2

- [x] T006 [P] [US2] Update `GlobalExceptionHandlerTest` to verify 404 and 422 mappings.
- [x] T007 [P] [US2] Update use-case tests (e.g., `AuthorizeTransactionUseCaseTest.java`) to expect the new domain exceptions.

### Implementation for User Story 2

- [x] T008 [P] [US2] Create `ResourceNotFoundException.java` extending `DomainException` in `src/main/java/com/corebank/domain/exception/ResourceNotFoundException.java`.
- [x] T009 [P] [US2] Create `BusinessRuleViolationException.java` extending `DomainException` in `src/main/java/com/corebank/domain/exception/BusinessRuleViolationException.java`.
- [x] T010 [US2] Update `GlobalExceptionHandler` to add `@ExceptionHandler` for `ResourceNotFoundException` (returns 404).
- [x] T011 [US2] Update `GlobalExceptionHandler` to add `@ExceptionHandler` for `BusinessRuleViolationException` (returns 422).
- [x] T012 [US2] Refactor `AuthorizeTransactionUseCase.java` (`src/main/java/com/corebank/application/command/AuthorizeTransactionUseCase.java`) and other relevant domain logic to throw the new exceptions instead of generic `IllegalArgumentException` or `RuntimeException`.

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently. Custom exceptions correctly translate to 404/422 HTTP responses.

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [x] T013 Run quickstart.md validation manually to ensure curl commands return expected outputs.
- [x] T014 Run `mvn clean test` to guarantee all tests (unit and integration) are passing with the new exceptions.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Foundational (Phase 2)**: BLOCKS all user stories
- **User Stories**: US1 and US2 can be implemented sequentially. It is recommended to implement US1 first to lay the groundwork for `ProblemDetail`, then US2.
- **Polish (Final Phase)**: Depends on all user stories being complete.

### Parallel Opportunities

- Creation of domain exceptions (T008, T009) can be done in parallel.
- Test implementations (T006, T007) can be done in parallel with exception class creation.

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 2: Foundational
2. Complete Phase 3: User Story 1 (Global fallback and Validation errors)
3. **STOP and VALIDATE**: Test User Story 1 independently

### Incremental Delivery

1. Complete MVP (US1).
2. Add User Story 2 (Custom Exceptions + Refactoring) → Test independently → Validate quickstart.md scenarios.
