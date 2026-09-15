# Quickstart: Validate Typed Transaction Types

## Prerequisites

- Java 21
- Maven wrapper available
- Docker services available when running Spring context tests

## Run Tests

```bash
./mvnw -q -Dtest=TransactionControllerTest,AuthorizeTransactionUseCaseTest test
```

On Windows PowerShell:

```powershell
./mvnw.cmd -q -Dtest=TransactionControllerTest,AuthorizeTransactionUseCaseTest test
```

## Manual Validation

1. Start infrastructure with `docker compose up -d`.
2. Start the application using `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`.
3. Open Swagger at `http://localhost:8080/swagger-ui/index.html`.
4. Submit an authorization request with `type: "DEBIT"`; it should preserve the existing authorization behavior.
5. Submit another request with `type: "TRANSFER"`; it should pass type validation and reach the use case.
6. Submit a request with `type: "PIX"`; it should pass type validation and preserve the existing authorization behavior.
7. Submit requests with `type: "debit"`, `type: " pix "`, an omitted type, and `null`; each should return `400` and must not authorize a transaction.
8. Inspect the Swagger request schema and confirm that the type field lists only `DEBIT`, `TRANSFER`, and `PIX`.

## Expected Results

- Supported values are accepted exactly as documented.
- Unsupported values fail at the request boundary with structured `400` errors.
- Valid values preserve the current success and insufficient-balance behavior.
- No authentication or authorization setup is required for this demonstration project.
