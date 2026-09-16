# Copilot Instructions for CoreBank

## Project purpose

This is a Java 21 / Spring Boot 3 banking backend following hexagonal architecture, CQRS-oriented reads, and eventual consistency via Redis projections and RabbitMQ outbox events.

## Critical conventions

- Keep domain and application layers free of infrastructure concerns.
- Prefer business logic in `domain/` and `application/`; controllers and persistence adapters should orchestrate, not own business rules.
- Treat Redis balance reads as eventually consistent; do not assume a write is immediately visible in the projection.
- Preserve the outbox event flow for transaction authorization and balance projection updates.
- When changing behavior, validate the matching feature under `specs/<id>/` before editing APIs or rules.

## Repository layout

- `src/main/java/com/corebank/domain` — business rules, exceptions, repositories, value objects
- `src/main/java/com/corebank/application` — use cases, queries, application events
- `src/main/java/com/corebank/infrastructure` — Spring config, web controllers, persistence adapters, RabbitMQ/Redis integrations
- `specs/` — feature requirements and validation criteria
- `README.md` — project overview and manual run instructions

## Coding expectations

- Use package names under `com.corebank.*`.
- Keep naming consistent: `UseCase` / `UseCaseImpl`, `Query` / `QueryImpl`.
- Add or update tests close to the behavior being modified.
- Do not add business logic to `infrastructure/web` or `infrastructure/config`.
- Do not change validation behavior or API contracts without checking the relevant spec.

## Validation

Use these commands when validating changes:

- `./mvnw test`
- `./mvnw -Dtest=NomeDoTeste test`
- `./mvnw spring-boot:run`

## Common pitfalls

- Redis lag is not necessarily a correctness bug; it is part of the event-driven read model.
- Transaction types are strict and case-sensitive: `DEBIT`, `TRANSFER`, `PIX`.
- Not-found and validation errors should flow through the existing domain and web exception handling.
- Account creation helpers under dev/test profiles are for local validation only.

## References

- [README.md](../README.md)
- [pom.xml](../pom.xml)
- [specs](../specs)
