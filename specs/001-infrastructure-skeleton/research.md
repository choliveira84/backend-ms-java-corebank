# Phase 0: Research & Decisions

## 1. Testcontainers for Integration Tests

- **Decision**: Adopt Testcontainers for integration tests in conjunction with JUnit 5.
- **Rationale**: The project explicitly relies on Docker for containerization and local parity. Testcontainers is the standard in the Spring Boot ecosystem for spinning up ephemeral containers (PostgreSQL, Redis, RabbitMQ) during test execution, ensuring tests are reliable and not dependent on external state.
- **Alternatives considered**: Relying on an externally running `docker-compose up` before tests (rejected as it is prone to environmental issues and state pollution).

## 2. Standard Docker Image Versions

- **Decision**: Use `postgres:15-alpine`, `redis:7-alpine`, and `rabbitmq:3-management-alpine`.
- **Rationale**: Alpine variants reduce image sizes. PostgreSQL 15 provides mature JSONB support which is critical for Event Sourcing. RabbitMQ management plugin allows for easy local inspection of queues and exchanges.
- **Alternatives considered**: Latest tags (rejected due to unpredictability and potential breaking changes).

## 3. Build Tool

- **Decision**: Maven (`pom.xml`).
- **Rationale**: Standard choice with vast tooling support and explicitly mentioned in most of the global AI skill configurations for Spring Boot.
- **Alternatives considered**: Gradle (viable, but Maven is slightly more standardized in conservative enterprise Java).
