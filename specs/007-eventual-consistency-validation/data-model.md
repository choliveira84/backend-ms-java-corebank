# Data Model: Eventual Consistency Validation

## Account

Represents the account whose authoritative write-side ledger and derived read-side balance are compared.

- `accountId`: account identifier
- `writeBalance`: current balance in the PostgreSQL ledger
- `projectedBalance`: latest balance stored in Redis
- `projectionUpdatedAt`: timestamp of the latest successful projection update

## Transaction Authorization

Represents a successful debit command that changes the authoritative account state.

- `transactionId`: unique transaction identifier
- `accountId`: account affected by the debit
- `amount`: positive debit amount
- `status`: successful authorization status
- `authorizedAt`: timestamp used to correlate write and projection timing

## Domain Event

Represents the event written to the outbox and transported through RabbitMQ.

- `eventId`: unique event identifier
- `transactionId`: transaction being propagated
- `accountId`: account whose projection must change
- `amount`: debit amount
- `timestamp`: event creation time
- `status`: final transaction status
- `processed`: outbox publication state

## Balance Projection

Represents the Redis read model returned by balance queries.

- `accountId`: projected account
- `availableBalance`: latest projected balance
- `lastUpdatedAt`: timestamp of the projection update

## Relationships and State Transitions

1. A successful Transaction Authorization updates the Account write balance and creates a Domain Event in the outbox.
2. The Domain Event moves from `processed = false` to `processed = true` after successful RabbitMQ publication.
3. RabbitMQ delivery causes the Balance Projection to be updated asynchronously.
4. A balance query returns the latest Balance Projection immediately, whether it is fresh or temporarily behind.
5. Projection lag is `now - lastUpdatedAt` or, for a transaction-specific check, the elapsed time from authorization/event creation until the matching projection is observed.
6. Lag at or below 5 seconds is accepted; lag above 5 seconds is stale and must be detectable by automated validation and operational logging.
