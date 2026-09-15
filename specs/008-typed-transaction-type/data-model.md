# Data Model: Typed Transaction Type

## Transaction Type

A finite transaction vocabulary shared by the authorization request and the transaction flow.

- `DEBIT`: debit transaction for a purchase or supported debit operation
- `TRANSFER`: transfer transaction, including the currently documented transfer use case
- `PIX`: PIX transfer transaction

The vocabulary is closed. Any new value requires an explicit definition change and API documentation update.

## Transaction Authorization Request

The request submitted to the authorization endpoint.

- `amount`: positive monetary amount
- `type`: one of the supported Transaction Type values; required

## Authorization Command

The immutable application command created from a validated request.

- `accountId`: account context from the request header
- `amount`: positive amount
- `type`: typed Transaction Type value

## Persisted Transaction and Domain Event

The transaction history and outbox event preserve the selected type as its enum name for compatibility with the existing persistence and JSON payload formats.

- `transactionId`: unique authorization identifier
- `accountId`: affected account
- `amount`: authorized amount
- `type`: `DEBIT`, `TRANSFER`, or `PIX`
- `status`: authorization status
- `timestamp`: event/history creation time

## Validation States

1. A recognized exact enum name passes request binding.
2. An omitted or `null` type fails required-field validation.
3. An unknown, blank, whitespace-padded, or differently capitalized type fails enum binding before the use case executes.
4. A valid type with insufficient balance continues to the existing business-rule rejection path.
