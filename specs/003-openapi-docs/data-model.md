# Data Model

*(No new domain models or database tables are introduced in this feature.)*

The existing models exposed via the OpenAPI specification are:

1. **TransactionRequest** (Record): `amount` (BigDecimal), `type` (String)
2. **AuthorizeTransactionResult** (Record): `transactionId` (UUID), `status` (String), `message` (String)
3. **BalanceProjection** (Record): `accountId` (UUID), `availableBalance` (BigDecimal), `lastUpdatedAt` (LocalDateTime)
