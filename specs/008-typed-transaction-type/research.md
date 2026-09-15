# Research: Typed Transaction Type

## Decision: Use one shared closed transaction-type vocabulary

**Rationale**: The existing requirements identify debit transactions and PIX transfers, while the API contract already uses `DEBIT` and `TRANSFER`. The clarification adds `PIX` as a supported value. A single enum gives the controller, application command, history, event payload, and OpenAPI schema the same finite vocabulary and makes adding a new value an explicit code change.

**Alternatives considered**: Keeping `String` with `@NotBlank` only validates presence, not membership. A controller-only enum with conversion back to arbitrary strings would leave downstream layers weakly typed and allow the invalid vocabulary problem to reappear internally.

## Decision: Use strict JSON enum binding at the API boundary

**Rationale**: Jackson/Spring MVC rejects unknown enum values, case variations, and whitespace-padded values during request deserialization. Missing or `null` values can be rejected with Bean Validation using `@NotNull`. The existing global exception handler already maps request binding and validation failures to structured HTTP 400 responses.

**Alternatives considered**: Custom normalization would silently accept values such as `debit` or ` DEBIT `, contradicting the requirement for exact supported values. A custom validator would duplicate standard enum conversion behavior without adding business value.

## Decision: Keep API and business behavior unchanged for valid requests

**Rationale**: The feature changes input safety, not transaction authorization rules. Valid `DEBIT`, `TRANSFER`, and `PIX` requests must still reach the existing use case, preserve the current response, and continue to apply insufficient-balance rules.

**Alternatives considered**: Introducing a new endpoint or changing error status semantics would expand the scope and risk regressions in the existing authorization contract.

## Decision: Document enum values in OpenAPI and README

**Rationale**: The project constitution requires every endpoint to be discoverable through OpenAPI. The request schema and examples must expose the allowed values so API consumers can avoid invalid requests before sending them.

**Alternatives considered**: Relying only on runtime error messages would make the contract less discoverable and leave Swagger consumers without a finite type description.
