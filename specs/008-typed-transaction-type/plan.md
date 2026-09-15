# Implementation Plan: Typed Transaction Type

**Branch**: `[008-typed-transaction-type]` | **Date**: 2026-09-15 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `/specs/008-typed-transaction-type/spec.md`

## Summary

Replace the unrestricted transaction-type string at the authorization boundary with a closed, shared enum containing `DEBIT`, `TRANSFER`, and `PIX`. Bind the request directly to the typed value, propagate that type through the immutable application command and transaction event/history flow, preserve valid authorization behavior, and document the enum values in OpenAPI and README.

## Technical Context

**Language/Version**: Java 21

**Primary Dependencies**: Spring Boot 3.3.0, Spring MVC/Jackson, Bean Validation, Springdoc OpenAPI, JUnit 5, Mockito

**Storage**: PostgreSQL transaction history and outbox payloads continue storing the enum name as text; no schema migration is required.

**Testing**: JUnit 5, Mockito, Spring Boot MockMvc tests, and existing authorization unit tests

**Target Platform**: Containerized Spring Boot web service

**Project Type**: Backend REST API using hexagonal architecture

**Performance Goals**: Reject invalid transaction types at request deserialization/validation without invoking the authorization use case; preserve existing authorization latency and behavior for valid types.

**Constraints**: Supported values are exactly `DEBIT`, `TRANSFER`, and `PIX`; matching is case-sensitive; no normalization; no authentication or authorization changes; existing response statuses and payloads remain stable.

**Scale/Scope**: One authorization request DTO, one application command, existing transaction history/outbox serialization, OpenAPI schema, README, and focused tests.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Hexagonal Architecture**: PASS. The shared type is a domain/application value used by the web adapter and use-case port; no infrastructure dependency is introduced.
- **Physical CQRS and Event Sourcing**: PASS. Transaction history and outbox flow remain unchanged apart from the stronger type representation.
- **Immutability**: PASS. The enum and existing Java records preserve immutable request/command modeling.
- **Unit Testing**: PASS. MockMvc and use-case tests cover accepted and rejected values.
- **OpenAPI**: PASS. The request schema will expose the finite enum values.
- **Controller Input Validation**: PASS. Request body remains annotated with `@Valid`, and missing/null values remain boundary errors.
- **Authentication boundary**: PASS. Authentication and authorization are out of scope.

## Project Structure

### Documentation (this feature)

```text
specs/008-typed-transaction-type/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
└── contracts/api.md
```

### Source Code

```text
src/
├── main/java/com/corebank/
│   ├── domain/transaction/           # shared transaction type vocabulary
│   ├── application/command/          # typed authorization command
│   └── infrastructure/web/           # request binding and OpenAPI schema
└── test/java/com/corebank/
    ├── application/command/          # command behavior tests
    └── infrastructure/web/           # MockMvc contract tests
```

**Structure Decision**: Keep the existing single Spring Boot service and package boundaries. Add the enum at the shared transaction domain boundary, update only the authorization path, and extend existing controller tests rather than creating a parallel API abstraction.

## Phase 0: Research Summary

- Use strict enum binding for exact JSON values; do not normalize case or whitespace.
- Reuse the current global exception handling so conversion and validation failures remain structured `400` responses.
- Preserve enum names as text in the existing transaction history and event JSON to avoid database migration and external contract churn.
- Use MockMvc to prove that invalid input does not invoke the mocked authorization use case.

## Phase 1: Design Summary

- Add a `TransactionType` enum with `DEBIT`, `TRANSFER`, and `PIX`.
- Change `TransactionController.TransactionRequest.type` and `AuthorizeTransactionCommand.type` to the enum.
- Serialize `type.name()` where the existing history entity and outbox payload require strings.
- Add OpenAPI schema metadata/examples showing the allowed values.
- Add tests for both supported values, unknown values, null/omitted values, case/whitespace variants, and preservation of valid business behavior.

## Post-Design Constitution Check

**Status**: PASS. The design uses a closed immutable value set, preserves hexagonal boundaries and existing API behavior, keeps request validation at the controller boundary, documents the contract through OpenAPI, and adds JUnit coverage without introducing authentication scope.

## Complexity Tracking

No constitution violations identified. No complexity exception is required.
