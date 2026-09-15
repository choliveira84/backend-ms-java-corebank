# Phase 0: Research & Decisions

## Context
The feature requires a way to seed test accounts into the database for API testing purposes, bypassing standard business logic restrictions.

## Technical Decisions

### 1. Controller Segregation and Security
- **Decision**: Create a dedicated `TestAccountController` instead of appending to `AccountController`. Restrict its loading using Spring Profiles (e.g., `@Profile({"dev", "test"})`).
- **Rationale**: Keeps testing backdoor logic out of the production API footprint. Using a Spring Profile guarantees this endpoint will never be loaded in a production environment (assuming production uses a different profile).
- **Alternatives considered**: Appending to `AccountController` with a feature flag (adds unnecessary branching logic to production code), or injecting raw SQL via tests (violates the requirement of having an API endpoint for it).

### 2. Architecture: IN and OUT Ports
- **Decision**: Create `CreateAccountUseCase` as an interface (IN port) with `CreateAccountUseCaseImpl` as its implementation. Utilize the existing `AccountLedgerRepository` (OUT port) and its existing implementation adapter.
- **Rationale**: Strict adherence to CoreBank Constitution v1.6.0 (Principle I), which mandates explicit interface/implementation splits for both Use Cases and Repositories.

### 3. Events and Side-effects
- **Decision**: Do not generate or publish `OutboxEvent`s or `TransactionHistory` records for the initial account creation in this test flow. However, **write synchronously to the Read Model (Redis)**.
- **Rationale**: This is a test seeding mechanism. The goal is purely to set the `AccountLedger` state. Triggering RabbitMQ side effects might pollute downstream systems during tests. However, writing synchronously to Redis (via `BalanceRepository`) is required so that subsequent test API calls (e.g., `GET /balance`) can instantly read the seeded balance without waiting for eventual consistency.
