# Feature Specification: OpenAPI Documentation

**Feature Branch**: `003-openapi-docs`

**Created**: 2026-09-15

**Status**: Draft

**Input**: User description: "implementar o springdoc-openapi e expor a documentação das rotas REST de transação e saldo"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View API Documentation (Priority: P1)

As a developer or integrator, I want to access a web-based, interactive documentation interface for the CoreBank API, so that I can understand how to authorize transactions and query balances without looking at the source code.

**Why this priority**: API consumers need clear documentation to successfully integrate with the platform. This reduces friction and support requests.

**Independent Test**: Can be fully tested by navigating to the documentation URL (e.g., `/swagger-ui.html`) in a browser and verifying that both endpoints (transactions and balances) are fully documented.

**Acceptance Scenarios**:

1. **Given** the application is running, **When** I navigate to the Swagger UI endpoint, **Then** I see the CoreBank API documentation dashboard.
2. **Given** I am on the Swagger UI dashboard, **When** I expand the transaction operations, **Then** I see the `POST /api/v1/transactions/authorize` endpoint with its request schema, headers, and possible response codes.
3. **Given** I am on the Swagger UI dashboard, **When** I expand the account operations, **Then** I see the `GET /api/v1/accounts/balance` endpoint with its request headers and response schema.

---

### User Story 2 - Export API Specification (Priority: P2)

As an API consumer or automated tool, I want to download the raw OpenAPI specification (JSON or YAML), so that I can generate client SDKs or import the contract into API testing tools like Postman.

**Why this priority**: Machine-readable contracts are essential for automated client generation and API governance.

**Independent Test**: Can be fully tested by performing a GET request to the OpenAPI spec endpoint (e.g., `/v3/api-docs`) and successfully parsing the resulting JSON/YAML payload.

**Acceptance Scenarios**:

1. **Given** the application is running, **When** I request the raw OpenAPI spec URL, **Then** the server responds with a valid OpenAPI v3 schema document.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST expose an interactive HTML interface for API documentation.
- **FR-002**: System MUST expose a machine-readable JSON/YAML endpoint containing the OpenAPI v3 specification.
- **FR-003**: The documentation MUST describe the `POST` transaction authorization endpoint, including expected headers (`X-Account-Id`), payload body, and response codes (200, 404, 422).
- **FR-004**: The documentation MUST describe the `GET` balance inquiry endpoint, including expected headers (`X-Account-Id`) and response codes (200, 404).
- **FR-005**: The API documentation MUST include basic metadata (Title, Version, Description).

### Key Entities

*(No new domain entities are created for this feature; the feature reflects existing APIs)*

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of the active REST endpoints (Transactions and Accounts) are visible and accurately described in the documentation UI.
- **SC-002**: A developer can successfully execute a test request against the API directly from the interactive documentation interface.
- **SC-003**: The generated JSON/YAML specification passes validation against the official OpenAPI v3 schema standard without errors.

## Assumptions

- The existing REST controllers (`TransactionController`, `AccountController`) will be used as the source for the documentation.
- Security schemas (like OAuth2 or JWT) are not explicitly documented yet, as the current endpoints rely on a simple `X-Account-Id` header (authentication is assumed to be handled upstream). Note: The `X-Account-Id` header will be explicitly documented as a required parameter.
