# Data Model: check-balance

This feature relies on the pre-existing `BalanceProjection` entity, which serves as the read model (Redis) for account balances.

## Entities

### `BalanceProjection` (Pre-existing)

- **accountId** (`UUID`): Unique identifier of the account.
- **availableBalance** (`BigDecimal`): The current consolidated balance.
- **lastUpdatedAt** (`LocalDateTime`): The timestamp of the last processed event that altered this balance.

*Note: No new entities are introduced in this feature.*
