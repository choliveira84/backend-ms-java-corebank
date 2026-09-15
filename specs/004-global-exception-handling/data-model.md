# Data Model: Global Exception Handling

*(Note: This feature does not introduce new persistent database entities. The "models" described here refer strictly to the standard JSON payload structure used for error responses).*

## ProblemDetail (RFC 7807)

The standardized error response will conform to the following schema natively provided by Spring Boot 3 `ProblemDetail`:

- `type` (String): A URI reference that identifies the problem type. Defaults to "about:blank".
- `title` (String): A short, human-readable summary of the problem type (e.g., "Not Found", "Unprocessable Entity").
- `status` (Integer): The HTTP status code (e.g., 404, 422, 500).
- `detail` (String): A human-readable explanation specific to this occurrence of the problem.
- `instance` (String): A URI reference that identifies the specific occurrence of the problem (usually the request path).

### Extensions (Custom Properties)

Spring's `ProblemDetail` allows adding custom properties dynamically. If a payload validation error occurs (e.g., `@Valid` fails), we may inject an `invalid_params` array containing field-specific errors.
