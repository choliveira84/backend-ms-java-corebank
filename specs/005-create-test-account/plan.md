# Implementation Plan: 005-create-test-account

**Branch**: `005-create-test-account` | **Date**: 2026-09-15 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/005-create-test-account/spec.md`

## Summary

Provide a dedicated API endpoint (`POST /api/v1/test/accounts`) to synchronously seed an `AccountLedger` record in PostgreSQL and the Read Model in Redis, bypassing typical domain events. This endpoint enables automated testing by setting up account preconditions without manual DB manipulation. It will be restricted via Spring Profiles (`dev` or `test`).

## Technical Context

**Language/Version**: Java 21

**Primary Dependencies**: Spring Boot 3.3.0, Spring Web

**Storage**: PostgreSQL (Write Model - `AccountLedger`) and Redis (Read Model - `BalanceRepository`)

**Testing**: JUnit 5, Mockito, Spring Boot Test

**Target Platform**: Backend microservice

**Project Type**: REST API

**Performance Goals**: < 200ms response time for account creation

**Constraints**: Must strictly adhere to Hexagonal Architecture constraints (interfaces for IN and OUT ports)

**Scale/Scope**: Dev/Test environment only; not for production

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Principle I (Hexagonal Architecture)**: Checked. Use Case will be an interface (`CreateAccountUseCase`) with an `Impl` class. OUT ports are already interfaces.
- **Principle II (Immutability)**: Checked. The request DTO and Use Case command will be modeled as `record`s.
- **Principle III (Exception Handling)**: Checked. We will throw `BusinessRuleViolationException` or `ResourceNotFoundException` allowing the `GlobalExceptionHandler` to translate them, keeping the Controller free of manual try/catches.

## Project Structure

### Documentation (this feature)

```text
specs/005-create-test-account/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/           # Phase 1 output (/speckit-plan command)
└── tasks.md             # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

### Source Code (repository root)

```text
src/main/java/com/corebank/
├── application/
│   └── command/
│       ├── CreateAccountUseCase.java
│       └── CreateAccountUseCaseImpl.java
└── infrastructure/
    └── web/
        └── TestAccountController.java
```

**Structure Decision**: A new `TestAccountController` will be created in the `infrastructure.web` package to keep test-only endpoints isolated from the core `AccountController`. The Use Case interfaces and implementations will reside in `application.command`.

## Complexity Tracking

*(No violations to justify)*
