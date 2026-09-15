# API Contracts: transaction-authorization

## 1. Authorize Transaction
**Endpoint**: `POST /api/v1/transactions/authorize`

### Headers
- `X-Account-Id`: `UUID` (Simulating the pre-authenticated context extraction from an API Gateway)

### Request Body
```json
{
  "amount": 150.00,
  "type": "DEBIT"
}
```

### Responses
**200 OK** (Authorized)
```json
{
  "transactionId": "uuid",
  "status": "AUTHORIZED",
  "message": "Transaction successful"
}
```

**422 Unprocessable Entity** (Insufficient Funds)
```json
{
  "status": "REJECTED",
  "error": "INSUFFICIENT_FUNDS"
}
```

---

## 2. Get Balance
**Endpoint**: `GET /api/v1/accounts/balance`

### Headers
- `X-Account-Id`: `UUID`

### Responses
**200 OK**
```json
{
  "accountId": "uuid",
  "availableBalance": 1000.50,
  "lastUpdatedAt": "2026-09-15T12:00:00Z"
}
```

**404 Not Found**
```json
{
  "error": "ACCOUNT_NOT_FOUND"
}
```
