# Feature Specification: Tipo de Transação Tipado

**Feature Branch**: `[008-typed-transaction-type]`

**Created**: 2026-09-15

**Status**: Draft

**Input**: User description: "no controller espera o tipo da transação como uma string. quero que ele espere um valor de um enum para evitar recebimentos de valores inexistentes"

## Clarifications

### Session 2026-09-15

- Q: O tipo `PIX` deve fazer parte dos valores aceitos pela API? → A: Sim; `PIX` deve ser incluído no enum de tipos suportados.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Aceitar somente tipos de transação válidos (Priority: P1)

Como cliente da API de autorização, quero informar o tipo da transação usando um conjunto fechado de valores reconhecidos pelo sistema, para evitar que valores inexistentes ou grafias inválidas alcancem o fluxo de negócio.

**Why this priority**: A validação na fronteira da API evita estados ambíguos, reduz erros de integração e protege a autorização contra comandos que não representam uma operação suportada.

**Independent Test**: Enviar requisições de autorização com `DEBIT`, `TRANSFER` e `PIX`, além de valores inválidos, e verificar que somente os tipos suportados seguem para autorização.

**Acceptance Scenarios**:

1. **Given** uma requisição de autorização com tipo `DEBIT`, **When** a API recebe o payload, **Then** a requisição é aceita pela validação de tipo e segue para o caso de uso.
2. **Given** uma requisição de autorização com tipo `TRANSFER`, **When** a API recebe o payload, **Then** a requisição é aceita pela validação de tipo e segue para o caso de uso.
3. **Given** uma requisição de autorização com tipo `PIX`, **When** a API recebe o payload, **Then** a requisição é aceita pela validação de tipo e segue para o caso de uso.
4. **Given** uma requisição de autorização com um tipo inexistente, **When** a API recebe o payload, **Then** a requisição é rejeitada como tipo não suportado, sem executar a autorização.

---

### User Story 2 - Informar claramente erros de tipo (Priority: P2)

Como consumidor da API, quero receber uma resposta de erro estruturada quando informar um tipo inexistente, para corrigir rapidamente a integração sem interpretar uma falha interna do sistema.

**Why this priority**: Mensagens previsíveis tornam o contrato da API descobrível e evitam que clientes confundam dados inválidos com indisponibilidade do serviço.

**Independent Test**: Enviar tipos vazios, nulos, com grafia incorreta e com diferença de capitalização, verificando o status HTTP e a estrutura do erro.

**Acceptance Scenarios**:

1. **Given** uma requisição com tipo desconhecido, **When** a validação ocorre na entrada, **Then** a API responde `400` com o formato estruturado de erro já adotado pelo projeto.
2. **Given** uma requisição sem o campo de tipo, **When** a validação ocorre na entrada, **Then** a API responde `400` e não chama a autorização da transação.
3. **Given** uma requisição com tipo `debit` em letras minúsculas, **When** a validação ocorre, **Then** a API rejeita o valor por não corresponder exatamente a um tipo suportado.

### Edge Cases

- O tipo é enviado como `null`.
- O campo de tipo é omitido do JSON.
- O tipo contém espaços antes ou depois do valor.
- O tipo usa capitalização diferente, como `debit` ou `Transfer`.
- O tipo é válido, mas o saldo é insuficiente; nesse caso, a validação de tipo deve passar e a regra de negócio deve continuar responsável pela rejeição.
- O contrato futuro pode adicionar novos tipos, mas a inclusão deve ser explícita e documentada.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The transaction authorization request MUST accept only the supported transaction types `DEBIT`, `TRANSFER`, and `PIX`.
- **FR-002**: The transaction authorization request MUST represent the transaction type as a closed set of named values rather than an unrestricted string.
- **FR-003**: The system MUST reject unknown, null, omitted, blank, whitespace-padded, or differently capitalized transaction types at the API boundary.
- **FR-004**: Invalid transaction types MUST return HTTP `400` using the existing structured error response format.
- **FR-005**: The system MUST NOT invoke the transaction authorization use case when the transaction type fails input validation.
- **FR-006**: Valid transaction types MUST preserve the existing authorization behavior, including balance validation and the current response contract.
- **FR-007**: The API documentation MUST enumerate the supported transaction type values and show them in the authorization request example/schema.
- **FR-008**: Adding a new transaction type MUST require an explicit change to the supported type definition and its API documentation.

### Key Entities *(include if data involved)*

- **Transaction Type**: A finite business vocabulary identifying the supported operation kind, currently `DEBIT`, `TRANSFER`, or `PIX`.
- **Transaction Authorization Request**: The client payload containing the amount and one supported transaction type.
- **Validation Error**: The structured response returned when the request contains a value outside the supported transaction vocabulary.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of authorization requests using `DEBIT`, `TRANSFER`, or `PIX` pass type validation and retain the existing business flow.
- **SC-002**: 100% of requests using unsupported, missing, null, blank, whitespace-padded, or differently capitalized types are rejected with HTTP `400` before authorization executes.
- **SC-003**: The OpenAPI contract lists all supported transaction types and contains no unrestricted free-text type description.
- **SC-004**: Existing valid authorization scenarios continue to pass without changing their successful response or insufficient-balance behavior.

## Assumptions

- The currently supported values are `DEBIT`, `TRANSFER`, and `PIX`, based on the existing transaction requirements and the clarification recorded in this session.
- Type matching is case-sensitive and does not trim or normalize client values.
- Real authentication and authorization remain outside the scope of this feature.
- The existing structured validation error format and HTTP status mapping are reused.
- Credit/deposit transaction types are not introduced by this feature.
