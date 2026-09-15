# Quickstart & Validation Guide: Infrastructure Skeleton

## Prerequisites
- Docker and Docker Compose installed and running.
- Java 21 SDK installed.

## 1. Provisioning Backing Services

To spin up PostgreSQL, Redis, and RabbitMQ:

```bash
docker-compose up -d
```

**Expected Outcome**:
- Three containers start successfully.
- You can access the RabbitMQ management UI at `http://localhost:15672`.

## 2. Validating the Application Skeleton

To ensure the application starts and connects to the services:

```bash
./mvnw spring-boot:run
```

**Expected Outcome**:
- Application starts without throwing any connection refused errors for JDBC, Redis, or AMQP.

## 3. Validating the Test Configuration

To execute the JUnit 5 tests (which utilize Testcontainers):

```bash
./mvnw test
```

**Expected Outcome**:
- Test suite executes successfully.
- Ephemeral Docker containers are spun up and torn down automatically during the test run.

## Cleanup

To stop and remove the local backing services:

```bash
docker-compose down -v
```
