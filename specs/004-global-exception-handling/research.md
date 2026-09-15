# Research: Global Exception Handling

## RFC 7807 (Problem Details for HTTP APIs) Implementation

- **Decision**: Use Spring Boot 3's built-in `ProblemDetail` support combined with `@ControllerAdvice`.
- **Rationale**: Spring Framework 6 and Spring Boot 3 natively support RFC 7807 via the `ProblemDetail` class and `ErrorResponse` interface. This provides a standardized JSON structure for errors (`type`, `title`, `status`, `detail`, `instance`) without requiring external dependencies, minimizing boilerplate.
- **Alternatives considered**:
  - *Custom Error DTO*: Rejected because it reinvents the wheel and breaks API standardization across the industry.
  - *Zalando Problem Library*: Rejected because it is largely deprecated/redundant now that Spring Boot 3 provides native support.

## Domain Exceptions Strategy

- **Decision**: Define a base `DomainException` extending `RuntimeException`, with specific subclasses like `ResourceNotFoundException` and `BusinessRuleViolationException`.
- **Rationale**: Core domain use-cases should not throw generic exceptions (`IllegalArgumentException` or `RuntimeException`) for expected business rule violations. Using a typed hierarchy allows the infrastructure layer to generically intercept and map them to appropriate HTTP codes (e.g., 422 for business rules, 404 for not found).
- **Alternatives considered**:
  - *Checked Exceptions*: Rejected because they clutter the code with `throws` declarations and try-catch blocks, which goes against modern Java and Clean Code practices.
  - *Returning Either/Result objects*: Rejected as it introduces heavy functional programming paradigms that deviate from the current architecture.
