# Quickstart: OpenAPI Documentation

Follow these steps to validate that the OpenAPI documentation is working.

## 1. Start the Application
Ensure your `docker-compose` infrastructure (PostgreSQL, Redis, RabbitMQ) is running, then start the Spring Boot app:
```bash
./mvnw spring-boot:run
```

## 2. Verify Swagger UI
Open your web browser and navigate to:
```text
http://localhost:8080/swagger-ui.html
```
**Expected Outcome**: You should see the Swagger UI dashboard with "CoreBank API" as the title. Both `account-controller` and `transaction-controller` should be visible.

## 3. Verify Raw JSON Spec
Run the following `curl` command to fetch the raw JSON specification:
```bash
curl -s http://localhost:8080/v3/api-docs | jq
```
**Expected Outcome**: A valid JSON document outlining the server paths, components, and schemas.
