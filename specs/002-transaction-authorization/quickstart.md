# Quickstart & Validation: transaction-authorization

This guide proves the feature works end-to-end.

## Prerequisites
1. Docker containers running (`docker-compose up -d`)
2. Application running (`./mvnw spring-boot:run`)

## Scenario 1: Setup Account
Since we need a balance to authorize a debit, insert a manual ledger entry in PostgreSQL:
```sql
INSERT INTO account_ledger (id, account_id, balance, version, updated_at)
VALUES ('uuid-1', 'account-123', 1000.00, 0, NOW());
```

## Scenario 2: Test Balance Inquiry (Fast Read)
```bash
curl -H "X-Account-Id: account-123" http://localhost:8080/api/v1/accounts/balance
```
**Expected**: `1000.00`

## Scenario 3: Test Debit Authorization (ACID Write)
```bash
curl -X POST http://localhost:8080/api/v1/transactions/authorize \
-H "Content-Type: application/json" \
-H "X-Account-Id: account-123" \
-d '{"amount": 250.00, "type": "DEBIT"}'
```
**Expected**: `200 OK` with `AUTHORIZED`.

## Scenario 4: Test Eventual Consistency (Redis Projection)
Wait 1-5 seconds, then query the balance again:
```bash
curl -H "X-Account-Id: account-123" http://localhost:8080/api/v1/accounts/balance
```
**Expected**: `750.00`.

## Scenario 5: Test Insufficient Funds
```bash
curl -X POST http://localhost:8080/api/v1/transactions/authorize \
-H "Content-Type: application/json" \
-H "X-Account-Id: account-123" \
-d '{"amount": 1000.00, "type": "DEBIT"}'
```
**Expected**: `422 Unprocessable Entity` with `INSUFFICIENT_FUNDS`.
