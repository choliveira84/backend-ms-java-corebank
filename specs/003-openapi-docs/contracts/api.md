# API Contracts (OpenAPI)

This feature generates the OpenAPI v3 contract dynamically at runtime. The exposed endpoints will be:

## 1. Swagger UI
- **Path**: `GET /swagger-ui.html` (and `/swagger-ui/index.html`)
- **Response**: HTML interface for interactive API exploration.

## 2. Raw OpenAPI Specification
- **Path**: `GET /v3/api-docs`
- **Response**: JSON document conforming to the OpenAPI v3 schema, detailing:
  - `POST /api/v1/transactions/authorize`
  - `GET /api/v1/accounts/balance`
