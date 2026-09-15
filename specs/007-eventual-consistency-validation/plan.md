# Implementation Plan: Eventual Consistency Validation

**Branch**: `[007-eventual-consistency-validation]` | **Date**: 2026-09-15 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `/specs/007-eventual-consistency-validation/spec.md`

## Summary

Validate that successful debit authorizations reach the Redis balance projection through the existing asynchronous RabbitMQ flow within the documented five-second tolerance. The implementation will make projection lag observable and testable, preserve the current API behavior of returning the latest available projection without waiting, and avoid adding real authentication or authorization to this employment-selection project.

## Technical Context

**Language/Version**: Java 21

**Primary Dependencies**: Spring Boot 3.3.0, Spring Data Redis, Spring AMQP, Spring Data JPA, Flyway, Springdoc OpenAPI

**Storage**: PostgreSQL authoritative write-side state and transaction history; Redis balance projection; RabbitMQ asynchronous event transport

**Testing**: JUnit 5, Mockito, Spring Boot test support; integration coverage should use the project Docker services when available

**Target Platform**: Containerized Spring Boot web service running with Docker Compose

**Project Type**: Backend web service with hexagonal architecture, CQRS read/write separation, and asynchronous event projection

**Performance Goals**: At least 95% of post-authorization balance projections become visible within five seconds in the validation scenario; balance reads remain non-blocking while the projection is within tolerance

**Constraints**: Preserve strong write-side authorization; accept bounded read-side staleness; do not add production authentication/authorization; retain records for commands, events, and DTOs; keep controller endpoints documented and validated

**Scale/Scope**: One balance projection and transaction flow in the existing single Spring Boot service; focused tests and minimal operational signals for projection lag

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Hexagonal Architecture**: PASS. Projection behavior remains behind the existing balance repository port and event listener adapter.
- **Physical CQRS and Event Sourcing direction**: PASS. PostgreSQL remains authoritative and Redis remains a derived read model propagated through RabbitMQ.
- **Immutability**: PASS. New test data and event contracts will use existing records where applicable; no Lombok or mutable DTOs are introduced.
- **Tolerant Eventual Consistency**: PASS. The five-second upper bound and immediate latest-projection response are explicit feature behavior.
- **Unit Testing**: PASS. Projection calculations, lag detection, and API behavior will receive JUnit 5 tests with Mockito where unit isolation is appropriate.
- **OpenAPI and validation**: PASS. No new endpoint is required; the existing balance endpoint remains documented and keeps header/input validation.
- **Authentication boundary**: PASS. Real authentication and authorization are explicitly out of scope.

## Project Structure

### Documentation (this feature)

```text
specs/007-eventual-consistency-validation/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/api.md
└── tasks.md              # created by /speckit.tasks
```

### Source Code (repository root)

```text
src/
├── main/java/com/corebank/
│   ├── application/event/       # event publication and balance projection update
│   ├── application/query/       # balance read use case
│   ├── domain/account/          # balance projection and repository port
│   ├── domain/transaction/      # transaction history and outbox domain
│   └── infrastructure/
│       ├── persistence/         # Redis/PostgreSQL adapters
│       └── web/                  # existing balance API
└── test/java/com/corebank/
    ├── application/             # projection and query unit tests
    └── infrastructure/          # balance API and integration tests
```

**Structure Decision**: Keep the existing single Spring Boot service and hexagonal package layout. Add focused tests beside the current application and infrastructure tests; only adjust production projection code where the tests expose a correctness or observability gap.

## Phase 0: Research Summary

- Confirm the existing flow: authorization updates PostgreSQL and writes an outbox event; the scheduled publisher sends the payload to RabbitMQ; the listener updates Redis; the balance query reads Redis.
- Treat the existing balance API behavior as the contract: return the latest available projection immediately and expose the projection timestamp already present in the response.
- Use a focused integration scenario with real PostgreSQL, RabbitMQ, and Redis when the Docker environment is available; retain unit tests for deterministic projection and stale-lag decisions.
- Keep the five-second threshold configurable through application configuration rather than scattering a magic number through production code or tests.

## Phase 1: Design Summary

- Add an explicit projection-lag policy/value to compare the projection timestamp with the authorization/event timestamp.
- Ensure projection updates are deterministic for the event data available today and do not turn a missing projection into a negative balance through an implicit zero baseline.
- Add automated coverage for immediate reads, eventual convergence, sequential transactions, and lag beyond five seconds.
- Add a small operational signal for stale projection detection using the existing logging approach; full metrics/tracing remain outside this feature.
- No new REST endpoint is needed, so the existing balance API contract is documented in [contracts/api.md](contracts/api.md).

## Post-Design Constitution Check

**Status**: PASS. The design preserves the existing hexagonal boundaries, CQRS separation, asynchronous RabbitMQ propagation, Redis read model, immutable record-based contracts, documented API behavior, and JUnit 5 testing standards. Real authentication and authorization remain explicitly out of scope.

## Complexity Tracking

No constitution violations identified. No complexity exception is required.
