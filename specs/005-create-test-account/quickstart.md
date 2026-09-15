# Quickstart & Validation Guide

## Prerequisites
- Application running locally (`mvn spring-boot:run` or via Docker).
- The application MUST be started with the `dev` or `test` profile enabled (e.g., `-Dspring.profiles.active=dev`).

## 1. Create a Test Account

```bash
ACCOUNT_ID=$(uuidgen)

curl -X POST http://localhost:8080/api/v1/test/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "'$ACCOUNT_ID'",
    "initialBalance": 5000.00
  }'
```

**Expected Outcome**:
```json
{
  "accountId": "<YOUR_UUID>",
  "balance": 5000.00,
  "status": "CREATED"
}
```

## 2. Verify Account Balance

You can verify the account exists by hitting the balance endpoint (note that balance queries hit Redis, so unless the test endpoint also populates Redis or the system relies on PG fallback for direct queries, this might require a workaround or direct DB check if Event Sourcing sync delays are involved. However, for a test endpoint, it might be beneficial to write to both PG and Redis synchronously to ensure immediate test readiness).

```bash
curl -X GET http://localhost:8080/api/v1/accounts/balance \
  -H "X-Account-Id: $ACCOUNT_ID"
```

**Expected Outcome**:
```json
{
  "accountId": "<YOUR_UUID>",
  "availableBalance": 5000.00,
  "lastUpdatedAt": "2026-09-15T..."
}
```
*(If it returns 404, it might mean the test account creation needs to bypass the RabbitMQ async flow and write directly to Redis to ensure tests aren't flaky.)*
