# Research: Eventual Consistency Validation

## Decision: Validate the existing asynchronous projection path end to end

**Rationale**: The current flow already separates write and read responsibilities: authorization persists the write-side state and an outbox record, the scheduled publisher sends the event to RabbitMQ, and the listener updates Redis. The feature should prove this behavior instead of introducing a second synchronization mechanism.

**Alternatives considered**: Testing only the controller or query use case would miss failures in event publication and projection synchronization. Replacing RabbitMQ with synchronous database reads would violate the existing CQRS and eventual-consistency architecture.

## Decision: Return the latest available projection immediately within the five-second window

**Rationale**: The existing balance API is a read-side query and must remain non-blocking. The accepted behavior is to return the current Redis projection, including its update timestamp, while asynchronous propagation catches up.

**Alternatives considered**: Waiting for RabbitMQ would increase latency and couple reads to the broker. Returning an error for normal bounded lag would make the documented eventual-consistency model unusable.

## Decision: Use five seconds as an explicit, testable policy threshold

**Rationale**: The constitution and requirements define five seconds as the maximum acceptable read-side delay. A named configuration/policy value makes the threshold visible to tests and avoids duplicated magic numbers.

**Alternatives considered**: Hard-coding five seconds in multiple tests and classes would make later changes error-prone. An unbounded delay would not satisfy the stated requirement.

## Decision: Combine unit tests with a real-service integration scenario

**Rationale**: Unit tests are appropriate for deterministic projection calculations, event parsing, and stale-lag classification. A real integration test is needed to validate PostgreSQL, RabbitMQ, and Redis cooperation and the actual convergence behavior.

**Alternatives considered**: Mocking every external component would provide fast tests but could not prove queue delivery, listener binding, serialization, or Redis persistence. A full end-to-end test for every edge case would be slower than necessary.

## Decision: Keep authentication and authorization out of scope

**Rationale**: The user clarified that this is an employment-selection project and does not require production identity or access control. The existing simplified account context remains sufficient for the feature demonstration.

**Alternatives considered**: Adding JWT or role-based authorization would expand the feature beyond item 4 and introduce unrelated security configuration and test setup.
