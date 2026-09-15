# API Contracts: Create Test Account

## POST `/api/v1/test/accounts`

**Description**: Creates a new test account with a specified initial balance. Restricted to `dev` or `test` profiles.

### Request Body

```json
{
  "accountId": "a3b8364e-251c-4340-9da2-a429188e6a21",
  "initialBalance": 1000.00
}
```

- `accountId`: UUID (Required)
- `initialBalance`: Decimal (Required, minimum 0.00)

### Responses

#### 201 Created

```json
{
  "accountId": "a3b8364e-251c-4340-9da2-a429188e6a21",
  "balance": 1000.00,
  "status": "CREATED"
}
```

#### 400 Bad Request
Triggered if `initialBalance` is negative or `accountId` is invalid. Uses the RFC 7807 Problem Detail format (handled by `GlobalExceptionHandler`).

#### 409 Conflict / 422 Unprocessable Entity
Triggered if an account with the requested `accountId` already exists. Uses the RFC 7807 Problem Detail format.
