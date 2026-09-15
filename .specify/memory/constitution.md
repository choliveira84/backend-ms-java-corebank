<!--
Sync Impact Report:
- Version change: 1.1.0 -> 1.2.0
- Modified principles:
  - Added: VII. AI Skill Utilization
- Added sections: N/A
- Removed sections: N/A
- Follow-up TODOs: N/A
-->
# CoreBank Constitution

## Core Principles

### I. Hexagonal Architecture (Ports and Adapters)
The core domain must be placed at the center of the application and must remain completely agnostic of infrastructure, databases, or external frameworks. All external interactions must happen through explicit ports and adapters.

### II. Physical CQRS & Event Sourcing
Write operations (10% of load) and read operations (90% of load) must be physically segregated to guarantee independent scalability and database load isolation. The absolute truth of the system is the immutable stream of past events (Event Sourcing) stored in PostgreSQL, not the consolidated balance.

### III. Immutability and Clean Design (Zero Boilerplate)
Native Java `records` MUST be used to model Commands, Events, and DTOs to guarantee immutability by design. The use of code injection libraries for data modeling (e.g., Lombok) is expressly vetoed. Standard classes are strictly reserved for Aggregate Roots, where complex state management is required.

### IV. Tolerant Eventual Consistency
The system embraces eventual consistency for read operations. The read data store (Redis) is allowed to have an acceptable delay of up to 5 seconds in relation to the write operations. Synchronization between write and read models must be done through asynchronous messaging (RabbitMQ).

### V. Containerization and Portability
The entire application ecosystem, including the backend service, PostgreSQL, Redis, and RabbitMQ, must be packaged in Docker containers. Local development must be orchestrated using Docker Compose to ensure resource isolation and environment parity.

### VI. Unit Testing (JUnit 5 & Mockito)
Unit tests MUST always be implemented for every new feature, ensuring robustness and validating business logic. All unit testing MUST be written using JUnit 5 and Mockito.

### VII. AI Skill Utilization
Development activities MUST leverage the globally installed AI skills (agentic tools) whenever appropriate. The availability of these global skills must be continuously verified, and they should be actively used to ensure maximum productivity and adherence to established patterns.

## Technology Stack & Infrastructure

- **Backend:** Java 17 or 21 with Spring Boot.
- **Write Data Store (Event Store):** PostgreSQL using `JSONB` columns for fast, typed, and flexible append-only storage of domain events.
- **Read Data Store (Cache/Projection):** Redis for O(1) response times on balance queries.
- **Messaging/Broker:** RabbitMQ for asynchronous propagation of domain events.
- **Object Mapping:** MapStruct for fluid transition of entities between ports and adapters.
- **Testing:** JUnit 5 and Mockito.

## Quality Attributes & Constraints

- **Transactional Integrity (ACID):** Authorization of debit transactions must be absolutely precise. Strong consistency is required when validating a Command against the Event Store.
- **Authentication Boundary:** The system assumes that upstream clients have already established a security context. Identifiers (`accountId`) must be extracted directly from the authenticated request.

## Governance

- The Constitution supersedes all other architectural decisions.
- Any changes to the core principles (e.g., introducing a new database paradigm, abandoning CQRS) require a MAJOR version bump and architectural review.
- PRs must be validated against the "Zero Boilerplate", "Immutability", and "Unit Testing" principles.
- Use `requisitos_corebank.md` and `manifesto_arquitetural_corebank.md` as foundational references for business and architectural guidelines.

**Version**: 1.2.0 | **Ratified**: 2026-09-15 | **Last Amended**: 2026-09-15
