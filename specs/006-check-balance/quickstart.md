# Quickstart & Validation Guide: check-balance

This guide provides steps to manually validate the Check Balance feature locally.

## Prerequisites

1. Application running locally (`mvn spring-boot:run` or via IDE).
2. Dependent infrastructure running via Docker Compose (Redis, PostgreSQL, RabbitMQ).
3. Tools: `curl` or Postman.

## Scenario 1: Successfully Querying an Existing Account

**Step 1: Create a test account (from previous feature)**

Create a test account with an initial balance of 500.00. We will define the UUID:

```bash
export ACCOUNT_ID="123e4567-e89b-12d3-a456-426614174000"

curl -X POST http://localhost:8080/api/v1/test/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "'$ACCOUNT_ID'",
    "initialBalance": 500.00
  }'
```

*(Expected output: 201 Created with account details)*

**Step 2: Query the balance**

```bash
curl -v -X GET http://localhost:8080/api/v1/accounts/$ACCOUNT_ID/balance
```

**Expected Outcome:**
HTTP Status: `200 OK`
Response Body:
```json
{
  "accountId": "123e4567-e89b-12d3-a456-426614174000",
  "availableBalance": 500.0,
  "lastUpdatedAt": "..."
}
```

## Scenario 2: Querying a Non-Existent Account

**Step 1: Query a random account ID**

```bash
export RANDOM_ID="999e4567-e89b-12d3-a456-426614174999"

curl -v -X GET http://localhost:8080/api/v1/accounts/$RANDOM_ID/balance
```

**Expected Outcome:**
HTTP Status: `404 Not Found`

## Scenario 3: Invalid UUID Format

**Step 1: Query an invalid account ID string**

```bash
curl -v -X GET http://localhost:8080/api/v1/accounts/invalid-id-format/balance
```

**Expected Outcome:**
HTTP Status: `400 Bad Request`
