# Implementation Plan: [FEATURE]

**Branch**: `[###-feature-name]` | **Date**: [DATE] | **Spec**: [link]

**Input**: Feature specification from `/specs/[###-feature-name]/spec.md`

**Note**: This template is filled in by the `/speckit-plan` command; its definition describes the execution workflow.

## Summary

The feature provides an API endpoint `GET /api/v1/accounts/{accountId}/balance` to instantly retrieve an account's available balance. Following the CQRS pattern defined in the Constitution, the query strictly hits the Redis read-optimized datastore via the existing `BalanceRepository` adapter, isolating read load from the PostgreSQL write database.

## Technical Context

**Language/Version**: Java 21
**Primary Dependencies**: Spring Boot 3.3.x, Spring Data Redis, Springdoc OpenAPI
**Storage**: Redis (via `BalanceRedisRepository`)
**Testing**: JUnit 5, Mockito, Testcontainers (Redis)
**Target Platform**: Docker containerized environment
**Project Type**: REST API Web Service
**Performance Goals**: < 50ms (P95) response time
**Constraints**: Fully tech-agnostic query resolution, relying only on Redis.
**Scale/Scope**: Extremely high throughput (90% of system load).

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Principle I (Hexagonal)**: Query implemented as an IN port (`GetAccountBalanceQuery`), decoupled from Redis logic which lives in the OUT port (`BalanceRepository`). [PASS]
- **Principle II (CQRS)**: The query solely hits Redis, totally isolating the PostgreSQL event store. [PASS]
- **Principle III (Zero Boilerplate)**: Query inputs/outputs are Java Records. [PASS]
- **Principle IV (Tolerant Eventual Consistency)**: Clients will receive 404 if eventual consistency hasn't caught up, respecting the max 5-second lag design. [PASS]
- **Principle VIII (OpenAPI)**: The new endpoint will explicitly mandate `@Operation`, `@ApiResponses`, and `@Tag`. [PASS]

## Project Structure

### Documentation (this feature)

```text
specs/006-check-balance/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/           # Phase 1 output (/speckit-plan command)
└── tasks.md             # Phase 2 output (/speckit-tasks command)
```

### Source Code (repository root)

```text
src/main/java/com/corebank/
├── application/
│   └── query/
│       ├── GetAccountBalanceQuery.java          # IN Port interface and DTOs
│       └── GetAccountBalanceQueryImpl.java      # Implementation calling BalanceRepository
├── infrastructure/
│   └── web/
│       └── AccountBalanceController.java        # REST Controller exposing the GET endpoint
```

**Structure Decision**: The logic will reside in the `application/query` package following Hexagonal Architecture. The endpoint is placed in the `infrastructure/web` package. We reuse the existing `BalanceRepository` and `BalanceRedisRepository`.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

*N/A - No violations.*
