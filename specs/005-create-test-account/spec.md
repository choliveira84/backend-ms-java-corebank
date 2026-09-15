# Feature Specification: create-test-account

**Feature Branch**: `005-create-test-account`

**Created**: 2026-09-15

**Status**: Draft

**Input**: User description: "quero um fluxo para criar uma conta no banco de dados para fins de testes de api. deve haver controller, use case, etc. siga tudo da constituição"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Create a test account (Priority: P1)

As a QA engineer or developer running automated API tests, I need an endpoint to programmatically create test accounts in the database with a specified initial balance and account ID, so that I can set up preconditions for testing transaction and balance endpoints.

**Why this priority**: Essential to unblock end-to-end API testing. Without this, tests rely on manual database seed scripts or mock data, reducing test reliability.

**Independent Test**: Can be tested independently by making a POST request to the endpoint and verifying that the account is created in PostgreSQL and is retrievable by the `/api/v1/accounts/balance` endpoint or directly in the DB.

**Acceptance Scenarios**:

1. **Given** a valid payload with a UUID and an initial balance, **When** I request to create a test account, **Then** the account is successfully created in the ledger.
2. **Given** an account ID that already exists in the system, **When** I request to create a test account with that ID, **Then** the system rejects the creation with a conflict error.
3. **Given** a negative initial balance, **When** I request to create a test account, **Then** the system rejects the payload with a validation error.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST expose an API endpoint (e.g., `POST /api/v1/test/accounts`) to create a new `AccountLedger` record in the database.
- **FR-002**: The request MUST accept an `accountId` (UUID) and an `initialBalance` (BigDecimal).
- **FR-003**: The endpoint MUST NOT be exposed in production environments (or must be restricted), as it bypasses standard account creation rules.
- **FR-004**: The system MUST validate that the initial balance is zero or greater.
- **FR-005**: The system MUST enforce Hexagonal Architecture as per Constitution v1.6.0: The Use Case MUST be an interface (`CreateAccountUseCase`) with a concrete implementation class, utilizing the existing `AccountLedgerRepository` (which is already an OUT port interface).
- **FR-006**: The input payload MUST be modeled as a Java `record` to guarantee immutability (Zero Boilerplate Principle).

### Key Entities

- **AccountLedger**: The core domain entity representing an account balance in the PostgreSQL event store / ledger.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Automated API test suites can create a fresh test account and receive a 201 Created or 200 OK response within 200ms.
- **SC-002**: Verification that the use case strictly adheres to the IN port interface pattern mandated by the Constitution.
- **SC-003**: The created account can immediately be used to authorize transactions via the existing transaction endpoints.

## Assumptions

- This endpoint is purely for testing and development, and we assume it will be guarded by a Spring profile (e.g., `@Profile("test")` or `@Profile("dev")`) or placed in a test-only controller to avoid accidental production use.
- No asynchronous events (Outbox Events) need to be published for the creation of a *test* account, as it is just seeding data for tests.
