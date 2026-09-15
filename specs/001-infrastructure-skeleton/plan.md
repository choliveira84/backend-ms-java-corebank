# Implementation Plan: Infrastructure Skeleton

**Branch**: `001-infrastructure-skeleton` | **Date**: 2026-09-15 | **Spec**: [specs/001-infrastructure-skeleton/spec.md](spec.md)

**Input**: Feature specification from `/specs/001-infrastructure-skeleton/spec.md`

## Summary

Provision local development infrastructure (PostgreSQL, Redis, RabbitMQ) using Docker Compose and bootstrap a Spring Boot Java backend conforming to Hexagonal Architecture, with connections to all backing services and basic unit test configurations.

## Technical Context

**Language/Version**: Java 21

**Primary Dependencies**: Spring Boot 3.x, Spring Data JDBC/JPA, Spring Data Redis, Spring AMQP

**Storage**: PostgreSQL (Write), Redis (Read)

**Testing**: JUnit 5, Mockito (NEEDS CLARIFICATION: Testcontainers for integration tests?)

**Target Platform**: Docker / Local OS

**Project Type**: Backend Web Service

**Performance Goals**: Local provisioning under 3 minutes

**Constraints**: Hexagonal architecture, No Lombok, Java records only

**Scale/Scope**: Initial infrastructure setup

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Hexagonal Architecture**: Yes, domain isolation is mandated.
- [x] **Zero Boilerplate (No Lombok)**: Yes, strictly enforced.
- [x] **Immutability**: Java records will be used.
- [x] **Unit Testing**: JUnit 5 and Mockito are mandated.
- [x] **AI Skill Utilization**: Mandated by SC-004.
- [x] **Containerization**: Docker Compose mandated.

## Project Structure

### Documentation (this feature)

```text
specs/001-infrastructure-skeleton/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/           # Phase 1 output (/speckit-plan command)
└── tasks.md             # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

### Source Code (repository root)

```text
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── corebank/
│   │           ├── domain/         # Pure Java, no framework
│   │           ├── application/    # Ports and Use cases
│   │           └── infrastructure/ # Adapters, Spring Boot, DB Config
│   └── resources/
│       └── application.yml
├── test/
│   └── java/
│       └── com/
│           └── corebank/
│               └── CorebankApplicationTests.java
docker-compose.yml
pom.xml
```

**Structure Decision**: Hexagonal package structure inside a standard Spring Boot application layout.
