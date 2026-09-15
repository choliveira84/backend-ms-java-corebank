# API Contract: Error Responses (Problem Details)

This contract defines the format that clients must expect when an API request fails.

## Scenario: 404 Not Found

**Trigger**: Client requests a resource (e.g., Account) that does not exist. (Throws `ResourceNotFoundException`)

**HTTP Status**: `404 Not Found`

**Response Body** (application/problem+json):
```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Account 12345 not found.",
  "instance": "/api/v1/accounts/balance"
}
```

## Scenario: 422 Unprocessable Entity (Business Rule)

**Trigger**: Client submits a valid payload, but it violates a domain business rule (e.g., insufficient funds). (Throws `BusinessRuleViolationException`)

**HTTP Status**: `422 Unprocessable Entity`

**Response Body** (application/problem+json):
```json
{
  "type": "about:blank",
  "title": "Unprocessable Entity",
  "status": 422,
  "detail": "Insufficient funds for transaction.",
  "instance": "/api/v1/transactions/authorize"
}
```

## Scenario: 400 Bad Request (Validation Error)

**Trigger**: Client submits an invalid payload missing required fields.

**HTTP Status**: `400 Bad Request`

**Response Body** (application/problem+json):
```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Invalid request content.",
  "instance": "/api/v1/transactions/authorize",
  "invalid_params": [
    {
      "name": "amount",
      "reason": "must not be null"
    }
  ]
}
```

## Scenario: 500 Internal Server Error

**Trigger**: An unhandled system error occurs.

**HTTP Status**: `500 Internal Server Error`

**Response Body** (application/problem+json):
```json
{
  "type": "about:blank",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred.",
  "instance": "/api/v1/transactions/authorize"
}
```
*(Note: Internal stack traces are intentionally omitted for security reasons).*
