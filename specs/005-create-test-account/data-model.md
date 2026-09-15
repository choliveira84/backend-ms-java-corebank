# Phase 1: Data Model

## Existing Entities Utilized

### AccountLedger
Represents the account state in the database.

**Fields**:
- `id` (UUID, Primary Key) - Internal database identifier
- `accountId` (UUID, Unique, Not Null) - Public identifier for the account
- `balance` (BigDecimal, Not Null) - Current account balance
- `version` (Integer, Not Null) - Optimistic locking version
- `updatedAt` (LocalDateTime, Not Null) - Timestamp of last update

**Validation Rules for this flow**:
- `initialBalance` provided in the request must be >= 0.
- `accountId` must be unique. If it already exists, the repository layer (or a prior check) will reject it to prevent overriding state.
