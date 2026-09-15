---
description: "Task list template for feature implementation"
---

# Tasks: Infrastructure Skeleton

**Input**: Design documents from `/specs/001-infrastructure-skeleton/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: The examples below include test tasks as requested by the constitution.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Initialize Maven Spring Boot 3.x project without Lombok in `pom.xml`
- [ ] T002 [P] Configure Maven to use Java 21 compiler source and target in `pom.xml`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T003 Add Spring Boot, DB Drivers, and Testcontainers dependencies to `pom.xml`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Local Development Environment Provisioning (Priority: P1) 🎯 MVP

**Goal**: Provision the local backing services with a single command so that the environment is fully operational.

**Independent Test**: Can be tested by running `docker-compose up -d` and ensuring all containers run.

### Implementation for User Story 1

- [ ] T004 [US1] Create `docker-compose.yml` with `postgres:15-alpine` container
- [ ] T005 [P] [US1] Append `redis:7-alpine` container to `docker-compose.yml`
- [ ] T006 [P] [US1] Append `rabbitmq:3-management-alpine` container to `docker-compose.yml`

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - Hexagonal Architecture Foundation (Priority: P1)

**Goal**: Build business logic strictly isolated from infrastructure components.

**Independent Test**: Can be verified by checking the directory structure and ensuring no framework imports in the domain.

### Implementation for User Story 2

- [ ] T007 [P] [US2] Create domain module package structure in `src/main/java/com/corebank/domain/`
- [ ] T008 [P] [US2] Create application module package structure in `src/main/java/com/corebank/application/`
- [ ] T009 [P] [US2] Create infrastructure module package structure in `src/main/java/com/corebank/infrastructure/`

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - Database Connectivity and Bootstrapping (Priority: P1)

**Goal**: Start the backend application and connect to primary data stores and broker.

**Independent Test**: Start application and verify logs for successful connection pools and broker connections.

### Tests for User Story 3

- [ ] T010 [US3] Create integration test using Testcontainers (JUnit 5 & Mockito ready) in `src/test/java/com/corebank/CorebankApplicationTests.java` to verify Spring Boot context loads with DBs.

### Implementation for User Story 3

- [ ] T011 [US3] Configure connection strings (JDBC, Redis, RabbitMQ) in `src/main/resources/application.yml`
- [ ] T012 [US3] Create Spring Boot application entry point in `src/main/java/com/corebank/infrastructure/CorebankApplication.java`

**Checkpoint**: All user stories should now be independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T013 Run quickstart.md validation instructions to verify e2e flow locally.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion
- **User Stories (Phase 3+)**: Depend on Foundational phase
- **Polish (Final Phase)**: Depends on all stories

### User Story Dependencies

- **US1 (P1)**: Independent
- **US2 (P1)**: Independent
- **US3 (P1)**: Depends on US2 structure and US1 running backing services for local testing.

### Parallel Opportunities

- US1 and US2 can run in parallel.
- Creating the 3 sub-packages for US2 can run in parallel.

## Parallel Example: User Story 2

```bash
# Create packages simultaneously:
Task: "Create domain module package structure"
Task: "Create application module package structure"
Task: "Create infrastructure module package structure"
```

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 & 2
2. Complete Phase 3: User Story 1 (Docker Compose)
3. **STOP and VALIDATE**: Test User Story 1 independently (`docker-compose up`)

### Incremental Delivery

1. Foundation ready
2. Add US1 → Validate Docker
3. Add US2 → Validate Structure
4. Add US3 → Validate Testcontainers & Context Load
