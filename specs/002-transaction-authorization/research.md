# Phase 0: Research & Decisions

## 1. Ensuring ACID between Database Write and Event Publication

- **Context**: We must authorize transactions and emit domain events without risk of dual-write failures (e.g., DB commits but RabbitMQ fails).
- **Decision**: Implement the **Transactional Outbox Pattern**.
- **Rationale**: Saving the transaction outcome and the corresponding domain event in the same PostgreSQL transaction guarantees ACID properties. A separate background worker (or Spring `@Scheduled` task) will poll the outbox table and publish to RabbitMQ, ensuring at-least-once delivery.
- **Alternatives considered**: 
  - Direct publication to RabbitMQ (rejected due to lack of atomic transactions across DB and message broker).
  - Change Data Capture (CDC) via Debezium (rejected as overkill for this initial skeleton, though a viable future migration).

## 2. Redis Data Structure for Balance Projection

- **Context**: We need a fast-read structure to serve balance inquiries within 50ms.
- **Decision**: Use a Redis Hash (`HSET`) or simple String key-value pair (`account:{accountId}:balance`). Let's use simple Key-Value for atomic increments/decrements if needed, or just overwriting the balance from the projection worker.
- **Rationale**: A simple `String` value parsed as `BigDecimal` (or stored as cents in `Long`) allows O(1) retrieval and is trivial to implement and scale.
- **Alternatives considered**: Redis Hashes (useful if we wanted to store multiple fields like `updatedAt`, `status`, which might be beneficial. We will actually use Hash to store `balance` and `lastUpdate` to satisfy the eventual consistency timestamp requirements).

## 3. Database Schema for Transaction / Event Store

- **Context**: How to model the PostgreSQL schema to support event sourcing and authorization.
- **Decision**: Two main tables: `account_balance` (for ACID validation against current balance) and `transaction_outbox` (for event publishing). Wait, if we use Event Sourcing, the source of truth is the events. However, validating against events directly is slow. We need an aggregate snapshot.
- **Rationale**: We will use a typical CQRS command-side table: `account_ledger` (id, account_id, balance, version) for optimistic locking and ACID authorization. And an `outbox_events` (id, aggregate_id, event_type, payload, processed) for the Domain Events.
- **Alternatives considered**: Pure Event Sourcing (reading all events to reconstruct balance - rejected due to performance constraints for high-throughput authorizations).

---
