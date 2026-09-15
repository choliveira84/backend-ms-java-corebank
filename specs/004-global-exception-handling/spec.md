# Feature Specification: Global Exception Handling

**Feature Branch**: `004-global-exception-handling`

**Created**: 2026-09-15

**Status**: Draft

**Input**: User description: "criar controller advice, excecoes customizadas e alterar no código atual"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Standardized API Error Responses (Priority: P1)

As an API consumer, I want all API errors to be returned in a predictable and standardized format, so that I can easily parse, log, and display meaningful error messages to end-users without having to handle varying response structures.

**Why this priority**: A standardized API contract for errors is critical for reliable client integrations and overall system robustness.

**Independent Test**: Can be fully tested by triggering various error conditions (e.g., requesting a non-existent account, sending invalid payload data) and verifying that the response body strictly adheres to the standardized error schema.

**Acceptance Scenarios**:

1. **Given** the application is running, **When** I request an account that does not exist, **Then** I receive a 404 HTTP status code and a JSON body containing a standard error format (e.g., timestamp, status, error, message, path).
2. **Given** the application is running, **When** I send a transaction authorization request that violates a business rule (e.g., insufficient funds), **Then** I receive a 422 HTTP status code and a JSON body with the standard error format detailing the business violation.

---

### User Story 2 - Custom Business Exceptions (Priority: P1)

As a developer, I want to throw domain-specific custom exceptions (e.g., `AccountNotFoundException`, `InsufficientFundsException`) within the business logic, so that the application can automatically translate them into appropriate HTTP status codes via a global Controller Advice, keeping controllers clean and free of try-catch blocks.

**Why this priority**: Enhances code maintainability, enforces the DRY principle, and correctly separates domain logic from web transport logic (Hexagonal Architecture).

**Independent Test**: Can be fully tested through unit tests verifying that throwing a specific custom exception in a service/use-case correctly propagates and is mapped to the expected HTTP status by the web layer.

**Acceptance Scenarios**:

1. **Given** existing business logic that throws a generic `RuntimeException` or `IllegalArgumentException`, **When** the code is refactored to use a specific custom exception, **Then** the application behavior remains correct but the resulting HTTP response is semantically accurate (e.g., 404 or 422 instead of a generic 500 or 400).

### Edge Cases

- What happens if an unexpected/unknown exception occurs? (It MUST be caught as a fallback and mapped to a 500 Internal Server Error, without exposing internal stack traces to the client).
- What happens if validation annotations (`@Valid`) fail on the controller payload? (It MUST return a 400 Bad Request with a standard format detailing which fields failed validation).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST intercept all unhandled exceptions at the controller level using a global exception handler (Controller Advice).
- **FR-002**: The system MUST return a unified, standardized JSON error response structure for all errors.
- **FR-003**: The system MUST define custom domain exceptions for known error scenarios (e.g., Not Found, Business Rule Violation).
- **FR-004**: The system MUST map custom domain exceptions to specific HTTP status codes (e.g., 404 for Not Found, 422 for Unprocessable Entity).
- **FR-005**: The system MUST be refactored to replace generic exception throwing in the current codebase with the newly created custom exceptions.

### Key Entities

*(No new domain entities are created for this feature; this focuses on error transport structures and exception classes)*

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of API endpoints return the standardized JSON error format when an error occurs.
- **SC-002**: 100% of existing generic exceptions (e.g., `RuntimeException` used for business errors) in the core domain use-cases are replaced by specific custom exceptions.
- **SC-003**: Automated tests (unit/integration) pass and explicitly verify that business rule violations return the correct HTTP status codes (e.g., 422, 404) rather than generic 500 errors.

## Assumptions

- The standard error format will include fundamental fields such as `timestamp`, `status`, `error`, `message`, and `path`, aligning with Spring Boot's default error attributes or RFC 7807 (Problem Details).
- Refactoring will be limited to the existing codebase (currently encompassing the transaction authorization and account balance features).
- Domain exceptions will be created within the core domain layer, while the Controller Advice will reside in the infrastructure web layer, strictly adhering to Principle I (Hexagonal Architecture).
