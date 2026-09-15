---
description: "Task list template for feature implementation"
---

# Tasks: OpenAPI Documentation

**Input**: Design documents from `/specs/003-openapi-docs/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/api.md, quickstart.md

**Tests**: Integration testing is implicit via the web endpoints, but no additional Java unit tests are required specifically for swagger doc generation as we rely on the framework.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Add `springdoc-openapi-starter-webmvc-ui` dependency to `pom.xml`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

- [ ] T002 Create global OpenAPI configuration bean in `src/main/java/com/corebank/infrastructure/config/OpenApiConfig.java` to define Title, Version, and Description (FR-005).

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 & 2 - View API Documentation & Export API Specification (Priority: P1 & P2)

**Goal**: Provide a web-based, interactive documentation interface for the CoreBank API and expose the raw OpenAPI v3 JSON specification.

**Independent Test**: Can be fully tested by navigating to `/swagger-ui.html` and fetching `/v3/api-docs`.

### Implementation for User Story 1 & 2

*(Note: Since springdoc-openapi handles both the UI and the JSON export from the same annotations, both stories are implemented via the same tasks.)*

- [ ] T003 [P] [US1] Annotate `TransactionController` (`src/main/java/com/corebank/infrastructure/web/TransactionController.java`) with `@Tag` and `@Operation` for the `POST /api/v1/transactions/authorize` endpoint, detailing headers (`X-Account-Id`) and response codes (FR-003).
- [ ] T004 [P] [US1] Annotate `AccountController` (`src/main/java/com/corebank/infrastructure/web/AccountController.java`) with `@Tag` and `@Operation` for the `GET /api/v1/accounts/balance` endpoint, detailing headers (`X-Account-Id`) and response codes (FR-004).

**Checkpoint**: At this point, User Story 1 and 2 should be fully functional and testable independently.

---

## Phase 4: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T005 Run the verification steps in `specs/003-openapi-docs/quickstart.md` manually to ensure the Swagger UI renders correctly without errors.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### Parallel Opportunities

- Annotating `TransactionController` (T003) and `AccountController` (T004) can be done in parallel once the foundation is laid.
