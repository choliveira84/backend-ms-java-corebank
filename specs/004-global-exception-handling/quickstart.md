# Quickstart: Validating Global Exception Handling

This guide explains how to verify that the API correctly returns standardized `ProblemDetail` responses for various error conditions.

## Prerequisites

- Application is running locally (`docker compose up -d` followed by `mvn spring-boot:run` or running through IDE).

## Validation Scenario 1: Resource Not Found (404)

1. Send a request to fetch the balance of a non-existent account:

```bash
curl -v -H "X-Account-Id: 00000000-0000-0000-0000-000000000000" http://localhost:8080/api/v1/accounts/balance
```

2. **Expected Outcome**:
   - HTTP Status: `404 Not Found`
   - Response contains `"title": "Not Found"` and `"status": 404`.

## Validation Scenario 2: Business Rule Violation (422)

1. Ensure the test account has $1000.00 (from previous seed).
2. Attempt to authorize a transaction for an amount greater than the balance (e.g., $9999.00):

```bash
curl -v -X POST http://localhost:8080/api/v1/transactions/authorize \
  -H "X-Account-Id: 22222222-2222-2222-2222-222222222222" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "33333333-3333-3333-3333-333333333333",
    "amount": 9999.00,
    "merchant": "Super Store"
  }'
```

3. **Expected Outcome**:
   - HTTP Status: `422 Unprocessable Entity`
   - Response contains `"title": "Unprocessable Entity"`, `"status": 422`, and detail mentioning insufficient funds.

## Validation Scenario 3: Validation Error (400)

1. Attempt to authorize a transaction with a missing payload field (e.g. omitting `amount`):

```bash
curl -v -X POST http://localhost:8080/api/v1/transactions/authorize \
  -H "X-Account-Id: 22222222-2222-2222-2222-222222222222" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "44444444-4444-4444-4444-444444444444",
    "merchant": "Super Store"
  }'
```

2. **Expected Outcome**:
   - HTTP Status: `400 Bad Request`
   - Response contains `"title": "Bad Request"` and details about the validation failure.
