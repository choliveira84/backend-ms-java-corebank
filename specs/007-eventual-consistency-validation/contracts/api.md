# API Contract: Balance Consistency

This feature does not add a new endpoint. It validates the existing balance query contract.

## Query Balance

`GET /api/v1/accounts/balance`

### Request

- Header: `X-Account-Id: <UUID>`
- No request body

### Success Response

`200 OK`

The response contains the account identifier, the latest available projected balance, and the timestamp of the projection update. The returned projection may be behind the most recent successful authorization for up to five seconds.

### Not Found Response

`404 Not Found` when no balance projection exists for the requested account.

### Consistency Behavior

- The endpoint returns the latest available Redis projection immediately.
- The endpoint does not wait for RabbitMQ propagation.
- The endpoint does not return an error solely because the projection is within the accepted five-second delay.
- A projection that remains stale beyond five seconds is an operational/test failure and must be detectable by the validation suite.

### Out of Scope

Real authentication and authorization are not part of this contract. The existing simplified account context is retained for demonstration purposes.
