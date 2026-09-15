# Implementation Plan: Global Exception Handling

**Branch**: `004-global-exception-handling` | **Date**: 2026-09-15 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/004-global-exception-handling/spec.md`

## Summary

Implement a global exception handling mechanism using Spring Boot's `@ControllerAdvice` to intercept unhandled exceptions and return standardized JSON error responses based on RFC 7807 (Problem Details). Create custom domain exceptions (e.g., `ResourceNotFoundException`, `BusinessRuleViolationException`) mapped to specific HTTP status codes, and refactor existing core domain code to use them.

## Technical Context

**Language/Version**: Java 17 or 21 (matching current project)

**Primary Dependencies**: Spring Web (`spring-webmvc` for `@ControllerAdvice` and `ProblemDetail`)

**Storage**: N/A

**Testing**: JUnit 5, Mockito, Spring Boot Test (`MockMvc`)

**Target Platform**: JVM (Dockerized Spring Boot Application)

**Project Type**: REST Web Service

**Performance Goals**: Minimal overhead for error serialization.

**Constraints**: Must strictly adhere to Hexagonal Architecture. Domain exceptions must reside in the `domain` layer and have no web dependencies. The `@ControllerAdvice` must reside in the `infrastructure.web` layer.

**Scale/Scope**: Impacts all existing and future REST controllers. Refactoring current Use Cases to use the new domain exceptions.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Principle I (Hexagonal Architecture)**: PASS. Domain exceptions will be created in the `domain` package with zero dependencies on Spring Web. The `GlobalExceptionHandler` (`@ControllerAdvice`) will be created in the `infrastructure.web` package to handle HTTP translation.
- **Principle III (Immutability & Zero Boilerplate)**: PASS. Custom exceptions will be standard Java classes (as `Throwable` cannot be a `record`), but they will remain clean without Lombok.
- **Principle IX (SOLID and Clean Code)**: PASS. Centralizing error handling (DRY) and making business rules explicit through typed exceptions.

## Project Structure

### Documentation (this feature)

```text
specs/004-global-exception-handling/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/           # Phase 1 output (/speckit-plan command)
│   └── api.md
└── tasks.md             # Phase 2 output (to be generated)
```

### Source Code (repository root)

```text
src/main/java/com/corebank/
├── domain/
│   └── exception/ (new)
│       ├── DomainException.java
│       ├── ResourceNotFoundException.java
│       └── BusinessRuleViolationException.java
└── infrastructure/
    └── web/
        └── exception/ (new)
            └── GlobalExceptionHandler.java
```

**Structure Decision**: The feature is strictly divided between the `domain` layer (for business exceptions) and the `infrastructure.web` adapter (for HTTP translation), respecting Hexagonal Architecture.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

*No violations.*
