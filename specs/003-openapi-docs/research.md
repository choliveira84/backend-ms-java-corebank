# Phase 0: Research & Technical Decisions

## Decision 1: OpenAPI Generation Library

- **Decision**: Use `springdoc-openapi-starter-webmvc-ui`.
- **Rationale**: It is the official and most widely adopted library for generating OpenAPI v3 documentation in Spring Boot 3 applications. It natively supports Java records and modern Spring annotations, eliminating the need for manual Swagger configurations.
- **Alternatives considered**: Springfox (deprecated and incompatible with Spring Boot 3).

## Decision 2: Documentation Location

- **Decision**: Annotations will be placed directly on the controller methods (`TransactionController` and `AccountController`) in the `infrastructure/web` package.
- **Rationale**: Since the controllers are the entry points (Adapters) in our Hexagonal Architecture, placing the UI documentation annotations there keeps the domain clean while providing context right at the boundary.
