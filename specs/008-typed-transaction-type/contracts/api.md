# API Contract: Typed Transaction Type

## Authorize Transaction

`POST /api/v1/transactions/authorize`

### Request

Header:

- `X-Account-Id: <UUID>`

Body:

```json
{
  "amount": 250.00,
  "type": "DEBIT"
}
```

The `type` field is required and accepts exactly:

- `DEBIT`
- `TRANSFER`
- `PIX`

Values are case-sensitive. Values such as `debit`, `Transfer`, ` pix `, empty strings, and `null` are invalid.

### Responses

`200 OK` for a valid type and successful authorization. The existing authorization response remains unchanged.

`400 Bad Request` when the type is missing, null, unknown, blank, whitespace-padded, or differently capitalized. The response uses the existing ProblemDetail structure and the authorization use case is not executed.

`404 Not Found` when the account does not exist after request validation succeeds.

`422 Unprocessable Entity` when the account exists but the business rule rejects the transaction, such as insufficient funds.

### OpenAPI Requirement

The generated schema for the request must enumerate `DEBIT`, `TRANSFER`, and `PIX` rather than describing `type` as unrestricted text.
