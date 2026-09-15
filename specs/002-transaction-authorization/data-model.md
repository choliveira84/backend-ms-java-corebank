# Data Model: transaction-authorization

## PostgreSQL (Command Side - System of Record)

### `account_ledger`
Maintains the authoritative balance for ACID validations.
- `id` (UUID, Primary Key)
- `account_id` (UUID, Unique Index)
- `balance` (Numeric/BigDecimal) - Consolidated balance
- `version` (Integer) - For Optimistic Locking (JPA `@Version`)
- `updated_at` (Timestamp)

### `transaction_history`
Immutable log of all requested transactions.
- `id` (UUID, Primary Key)
- `account_id` (UUID, Foreign Key)
- `amount` (Numeric/BigDecimal)
- `type` (String) - e.g., DEBIT, TRANSFER
- `status` (String) - AUTHORIZED, REJECTED
- `created_at` (Timestamp)

### `outbox_events`
Ensures transactional domain event publishing.
- `id` (UUID, Primary Key)
- `aggregate_id` (UUID) - Usually the `account_id` or `transaction_id`
- `event_type` (String) - e.g., `TransactionAuthorizedEvent`
- `payload` (JSONB) - The event data
- `processed` (Boolean) - Default `false`
- `created_at` (Timestamp)

---

## Redis (Query Side - Read Projection)

### Hash: `account:{accountId}:balance`
Optimized read store for high-speed inquiries.
- Field: `availableAmount` (String representing BigDecimal)
- Field: `lastUpdatedAt` (String ISO-8601 Timestamp)

---

## RabbitMQ (Message Broker)

### Exchange: `corebank.events.exchange` (Topic)
- Routing Key: `transaction.authorized`
- Routing Key: `transaction.rejected`

## Event Schema

### `TransactionAuthorizedEvent`
```json
{
  "eventId": "uuid",
  "transactionId": "uuid",
  "accountId": "uuid",
  "amount": 100.50,
  "timestamp": "2026-09-15T12:00:00Z"
}
```
