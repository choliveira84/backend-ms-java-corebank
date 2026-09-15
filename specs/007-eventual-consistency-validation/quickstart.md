# Quickstart: Validate Eventual Consistency

## Prerequisites

- Java 21
- Docker and Docker Compose
- Repository checked out at the feature branch

## Start Infrastructure

```bash
docker compose up -d postgres redis rabbitmq
```

## Run the Automated Suite

```bash
./mvnw test
```

On Windows PowerShell, use:

```powershell
./mvnw.cmd test
```

## Validation Scenarios

1. Create a test account through `POST /api/v1/test/accounts` while the `dev` or `test` profile is active.
2. Authorize a debit through `POST /api/v1/transactions/authorize` with the `X-Account-Id` header.
3. Query `GET /api/v1/accounts/balance` immediately and confirm that the endpoint returns the latest available projection without waiting for the broker.
4. Poll the balance query until the debit is visible and verify that convergence occurs within five seconds in the controlled test environment.
5. Execute sequential debits and verify that the projection remains coherent with the authoritative transaction history.
6. Simulate or inject a delayed/failing projection and verify that the automated validation flags lag beyond five seconds.

## Expected Results

- The write-side authorization remains strongly consistent.
- The read-side query remains responsive during bounded propagation delay.
- The projection converges through RabbitMQ and Redis within the accepted threshold.
- Delays beyond five seconds are visible as test or operational failures.
- No real authentication or authorization setup is required.

See [contracts/api.md](contracts/api.md) for the endpoint contract and [data-model.md](data-model.md) for state and timing semantics.
