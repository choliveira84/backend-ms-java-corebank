# API Contract: check-balance

## `GET /api/v1/accounts/{accountId}/balance`

Retrieves the current available balance for a specific account.

### Path Parameters

- `accountId` (UUID, required): The unique identifier of the account.

### Responses

#### 200 OK

Returns the balance information for the requested account.

**Content-Type**: `application/json`

```json
{
  "accountId": "550e8400-e29b-41d4-a716-446655440000",
  "availableBalance": 500.00,
  "lastUpdatedAt": "2026-09-15T10:00:00.000Z"
}
```

#### 400 Bad Request

The `accountId` format is invalid (e.g., not a valid UUID).

#### 404 Not Found

The account could not be found in the read database. Note: this might happen if the account was very recently created and the read model hasn't been synchronized yet (eventual consistency).
