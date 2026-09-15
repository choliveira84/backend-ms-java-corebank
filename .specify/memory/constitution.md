<!--
Sync Impact Report:
- Version change: 1.6.0 -> 1.7.0
- Modified principles: N/A
- Added sections:
  - X. Controller Input Validation
- Removed sections: N/A
- Follow-up TODOs: N/A
-->
# CoreBank Constitution

## Core Principles

### I. Hexagonal Architecture (Ports and Adapters)
The core domain must be placed at the center of the application and must remain completely agnostic of infrastructure, databases, or external frameworks. All external interactions must happen through explicit ports (IN and OUT) and adapters. There must be clear divisions between IN and OUT:
- **IN Ports (Use Cases)**: Every use case MUST be defined as an interface (the port) residing in the application layer, with a corresponding concrete implementation class.
- **OUT Ports (Repositories/External Services)**: Every repository or external dependency MUST be defined as an interface (the port) within the domain or application layer. The concrete implementation class (the adapter) must reside in the infrastructure layer. For example, a repository adapter implements the domain's repository interface and internally uses framework-specific tools like a Spring Data JPA interface.

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

### VIII. API Documentation (OpenAPI)
The application API MUST be documented and exposed using the OpenAPI specification. This ensures a standardized, discoverable, and interactive contract for all REST endpoints, facilitating upstream integration and front-end development.

### IX. SOLID and Clean Code Principles
The entire application MUST strictly adhere to SOLID foundations, general software engineering best practices, and well-established design patterns. Development MUST follow DRY (Don't Repeat Yourself) to minimize code duplication and YAGNI (You Aren't Gonna Need It) to prevent over-engineering and premature optimization.

### X. Controller Input Validation
Every controller endpoint that receives a request body MUST validate the payload using Java Bean Validation annotations (e.g., `@Valid` or `@Validated`). This ensures that invalid data is rejected at the system boundaries before reaching the application layer.

## Technology Stack & Infrastructure

- **Backend:** Java 17 or 21 with Spring Boot.
- **API Documentation:** OpenAPI (springdoc-openapi) for interactive endpoint exposure.
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

**Version**: 1.7.0 | **Ratified**: 2026-09-15 | **Last Amended**: 2026-09-15
