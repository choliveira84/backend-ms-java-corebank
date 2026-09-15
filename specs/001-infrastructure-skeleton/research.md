# Phase 0: Research & Decisions

## 1. Integration Tests

- **Question**: Are we utilizing Testcontainers for the integration tests?
- **Decision**: No. We will rely on the local `docker-compose` infrastructure and standard `@SpringBootTest` instead of Testcontainers.
- **Reasoning**: Testcontainers was originally selected but failed due to local Docker daemon Named Pipe socket constraints in the development environment. Relying on the static `docker-compose` containers provides a simpler testing approach while still verifying integration with real database instances.
- **Next Steps**: Removed Testcontainers dependencies from `pom.xml`.

## 2. Standard Docker Image Versions

- **Decision**: Use `postgres:15-alpine`, `redis:7-alpine`, and `rabbitmq:3-management-alpine`.
- **Rationale**: Alpine variants reduce image sizes. PostgreSQL 15 provides mature JSONB support which is critical for Event Sourcing. RabbitMQ management plugin allows for easy local inspection of queues and exchanges.
- **Alternatives considered**: Latest tags (rejected due to unpredictability and potential breaking changes).

## 3. Build Tool

- **Decision**: Maven (`pom.xml`).
- **Rationale**: Standard choice with vast tooling support and explicitly mentioned in most of the global AI skill configurations for Spring Boot.
- **Alternatives considered**: Gradle (viable, but Maven is slightly more standardized in conservative enterprise Java).
