---
name: corebank-transaction-flow
description: "Use when working on balance queries, transaction authorization, account creation, Redis projections, outbox events, or eventual-consistency behavior in the CoreBank Java project."
---

# CoreBank transaction flow

## Project context

This repository implements a banking workflow with hexagonal architecture and eventual consistency:

- `domain/` contains business rules, exceptions, ports, and value objects
- `application/` contains use cases and query handlers
- `infrastructure/` contains adapters, Spring configuration, controllers, and persistence
- `specs/<id>/` is the source of truth for behavioral changes

## Required behavior to preserve

- Preserve the separation between business logic and infrastructure
- Keep transaction validation strict and domain-driven
- Treat the Redis balance projection as eventually consistent, not instantly consistent
- Preserve the outbox + projection pipeline for transaction authorization
- Do not change API contracts without updating the corresponding spec and docs

## Key flow areas

- Account creation: `CreateAccountUseCaseImpl`
- Balance query: `GetAccountBalanceQueryImpl`
- Transaction authorization: `AuthorizeTransactionUseCaseImpl`
- Event publishing: `OutboxEventPublisher`
- Projection update: `BalanceProjectionUpdater`
- Consistency checks: `ProjectionConsistencyPolicy`

## Domain rules

- Account IDs are required in request headers for read and write flows
- Transaction types are strict and case-sensitive: `DEBIT`, `TRANSFER`, `PIX`
- Insufficient balance must reject authorization according to domain rules
- Not-found cases must map to the existing domain exception flow
- Immediate reads after authorization may still reflect the last Redis projection; the projection is expected to converge shortly afterward

## Editing guidance

- Prefer updates in the relevant domain/application layer before touching adapters
- If a change affects behavior, confirm the matching feature folder under `specs/`
- Keep naming conventions consistent with `UseCase` / `UseCaseImpl` and `Query` / `QueryImpl`
- Add or update tests close to the behavior being modified

## Validation commands

- Run the full suite: `./mvnw test`
- Run a single test class: `./mvnw -Dtest=NomeDoTeste test`
- Run the app locally: `./mvnw spring-boot:run`

## Common pitfalls

- Do not place business logic in `infrastructure/web` or `infrastructure/config`
- Do not treat Redis lag as a correctness failure without checking the projection policy
- Do not bypass the spec documents when changing API behavior or validation outcomes
