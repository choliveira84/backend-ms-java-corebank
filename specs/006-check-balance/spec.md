# Feature Specification: check-balance

**Feature Branch**: `006-check-balance`

**Created**: 2026-09-15

**Status**: Draft

**Input**: User description: "vamos para o item 3 dos requisitos (RF03 - Consulta Rápida de Saldo)"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Consulta Rápida de Saldo (Priority: P1)

Como um cliente final ou sistema interfaceador, eu desejo consultar o saldo atualizado de uma conta bancária para visualizá-lo instantaneamente na tela.

**Why this priority**: É a operação de leitura mais demandada (representando cerca de 90% da carga do sistema, conforme Constituição), essencial para o funcionamento do app do cliente.

**Independent Test**: Pode ser testado através da interface de API consultando uma conta específica e verificando se o saldo retornado reflete as operações anteriores processadas.

**Acceptance Scenarios**:

1. **Given** uma conta bancária ativa com saldo pré-existente de 500.00, **When** o cliente consulta o saldo dessa conta, **Then** o sistema retorna `500.00` imediatamente.
2. **Given** um identificador de conta que não existe no sistema, **When** o cliente tenta consultar o saldo, **Then** o sistema retorna uma mensagem clara de que a conta não foi encontrada.

---

### Edge Cases

- What happens when a conta foi criada, mas a consistência eventual ainda não sincronizou o saldo para a base de leitura?
  - A consulta deve retornar o status de conta não encontrada. O cliente (aplicativo frontend) deve ser instruído a tentar novamente, seguindo a premissa arquitetural de Tolerant Eventual Consistency.
- How does system handle identificador de conta malformado?
  - A chamada deve ser rejeitada imediatamente na borda da aplicação indicando erro de validação (Bad Request).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: O sistema MUST fornecer uma interface de API para consulta do saldo por identificador de conta.
- **FR-002**: A operação de consulta MUST ser resolvida através de uma base de dados projetada exclusivamente para leitura, sem causar qualquer impacto de processamento no banco de dados transacional de escrita.
- **FR-003**: A interface de consulta MUST ser interativamente documentada e exposta em um catálogo de APIs padronizado para integração.
- **FR-004**: O sistema MUST validar a existência da conta requisitada, emitindo feedback apropriado caso a mesma não seja encontrada na base de consulta rápida.

### Key Entities

- **BalanceProjection**: Representação estruturada contendo o identificador único da conta e seu respectivo saldo disponível para leitura imediata.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Tempo de resposta do endpoint de consulta de saldo deve ser consistentemente menor que 50ms para 95% das requisições (P95).
- **SC-002**: A funcionalidade deve conseguir suportar alta concorrência simulada (isolamento de leitura) sem degradar o tempo de resposta ou repassar carga ao banco principal de escrita.

## Assumptions

- Assumimos que o `accountId` será passado via Path Variable e já foi validado pelo middleware de segurança do API Gateway (Authentication Boundary), embora devamos validar seu formato lógico.
- Como esta é uma funcionalidade focada em CQRS Query, não há Command, nem persistência no PostgreSQL ou disparos de eventos para o RabbitMQ envolvidos nesta task específica.
