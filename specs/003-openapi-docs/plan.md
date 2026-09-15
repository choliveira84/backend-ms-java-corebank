# Implementation Plan: OpenAPI Documentation

**Branch**: `003-openapi-docs` | **Date**: 2026-09-15 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/003-openapi-docs/spec.md`

## Summary

Implement `springdoc-openapi-starter-webmvc-ui` to generate and expose the OpenAPI v3 documentation for the CoreBank REST API endpoints (`TransactionController` and `AccountController`).

## Technical Context

**Language/Version**: Java 21

**Primary Dependencies**: `springdoc-openapi-starter-webmvc-ui` (Spring Boot 3)

**Storage**: N/A

**Testing**: JUnit 5, Spring Boot Test

**Target Platform**: JVM (Dockerized Spring Boot Application)

**Project Type**: REST Web Service

**Performance Goals**: Minimal overhead during startup; no overhead at runtime.

**Constraints**: Must follow Constitution Principle VIII (OpenAPI Documentation). Must integrate smoothly with Java `record` dtos without boilerplate.

**Scale/Scope**: 2 REST Controllers (`TransactionController`, `AccountController`)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Principle I (Hexagonal Architecture)**: PASS. UI/Web annotations (`@Operation`, `@Schema`) will be restricted to the `infrastructure.web` and `infrastructure.config` packages.
- **Principle III (Zero Boilerplate)**: PASS. `springdoc-openapi` natively supports Java records.
- **Principle VIII (API Documentation)**: PASS. This feature explicitly implements this principle.

## Project Structure

### Documentation (this feature)

```text
specs/003-openapi-docs/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── api.md
└── tasks.md
```

### Source Code (repository root)

```text
src/main/java/com/corebank/infrastructure/
├── config/
│   └── OpenApiConfig.java (new - global info)
└── web/
    ├── TransactionController.java (modified - add annotations)
    └── AccountController.java (modified - add annotations)

pom.xml (modified - add springdoc dependency)
```

**Structure Decision**: Single project. Changes are strictly confined to the `infrastructure` adapters (Web/Config) and the Maven `pom.xml`, fully respecting Hexagonal Architecture.
