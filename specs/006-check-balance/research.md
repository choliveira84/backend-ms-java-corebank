# Research & Technical Decisions: check-balance

This document captures technical decisions and research findings made during the planning phase for the feature.

## Architectural Decisions

- **Decision:** Use Hexagonal Architecture's Query Port (CQRS)
- **Rationale:** The system implements physical CQRS. Checking a balance is a pure read operation and thus should not involve the Command side or the Event Store (PostgreSQL). It accesses the read projection (Redis).
- **Alternatives considered:** Calling the repository directly from the controller (rejected due to Hexagonal Architecture constraints requiring an IN port for Use Cases/Queries).

- **Decision:** Return HTTP 404 when eventual consistency is lagging
- **Rationale:** The Constitution allows up to 5 seconds of latency in the read model. If the account was just created, it might not be in Redis yet. A 404 is the semantically correct HTTP status code for a missing resource, instructing the client to retry.
- **Alternatives considered:** Falling back to PostgreSQL on cache miss (rejected due to CQRS strict isolation).
