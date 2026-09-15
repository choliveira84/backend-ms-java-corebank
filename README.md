# backend-ms-java-corebank

Backend do CoreBank em Java com Spring Boot, seguindo princípios de arquitetura hexagonal, CQRS, consistência eventual e documentação OpenAPI.

## Visão geral

Este projeto implementa a base de um sistema bancário com foco em:

- arquitetura hexagonal com ports e adapters;
- persistência transacional em PostgreSQL para eventos e escrituras;
- projeção de leitura em Redis para consultas rápidas de saldo;
- comunicação assíncrona por RabbitMQ;
- documentação interativa da API com Springdoc OpenAPI;
- testes unitários e de integração com JUnit 5 e Mockito.

## Feature atual

- Consulta rápida de saldo: `GET /api/v1/accounts/balance` com header `X-Account-Id`
- Autorização de transação: `POST /api/v1/transactions/authorize` com header `X-Account-Id`
- Criação de conta de teste em ambiente local: `POST /api/v1/test/accounts` em perfis `dev` e `test`
- Cenários cobertos: sucesso, conta inexistente, erro de validação e autorização rejeitada por saldo insuficiente

## Swagger

A documentação interativa da API fica disponível em:

- http://localhost:8080/swagger-ui/index.html

## Controller de apoio para testes em dev/test

Há um controller disponibilizado no contexto de desenvolvimento ou testes para facilitar a criação de contas de teste. Esse endpoint foi pensado para permitir que você gere uma conta rapidamente e, em seguida, utilize a API via Swagger ou Postman sem depender de um fluxo completo de onboarding.

Endpoint disponível em perfis `dev` e `test`:

```http
POST /api/v1/test/accounts
```

Exemplo de payload:

```json
{
  "initialBalance": 1000.00
}
```

Exemplo de uso:

- acesse a documentação OpenAPI no Swagger em http://localhost:8080/swagger-ui/index.html;
- localize o endpoint de criação de conta de teste;
- envie o payload acima para criar a conta;
- use o `accountId` retornado para chamar a consulta de saldo em `GET /api/v1/accounts/balance` com o header `X-Account-Id`;
- também é possível criar transações via `POST /api/v1/transactions/authorize` usando o mesmo header.

Esse mecanismo é útil para validar cenários de integração, testar fluxos de leitura e executar chamadas manuais em ambiente local sem precisar montar todo o processo operacional da aplicação.

## Contrato principal da API

- `GET /api/v1/accounts/balance`
  - Header obrigatório: `X-Account-Id: <UUID>`
  - Respostas esperadas: `200` e `404`

- `POST /api/v1/transactions/authorize`
  - Header obrigatório: `X-Account-Id: <UUID>`
  - Body esperado:
    ```json
    {
      "amount": 250.00,
      "type": "DEBIT"
    }
    ```
  - Respostas esperadas: `200`, `400`, `404` e `422`

## Stack

- Java 17 / 21
- Spring Boot 3.x
- PostgreSQL
- Redis
- RabbitMQ
- Docker Compose
- Springdoc OpenAPI

## Como executar

### 1) Subir infraestrutura local

```bash
docker compose up -d
```

### 2) Rodar a aplicação

```bash
./mvnw spring-boot:run
```

### 3) Executar testes

```bash
./mvnw test
```

## Status do projeto

- Estrutura inicial de infraestrutura pronta
- Feature de consulta de saldo em andamento/implementada conforme o plano de especificação
- Documentação de API publicada via OpenAPI

## Referências

- Especificação da feature de saldo: specs/006-check-balance/spec.md
- Plano da implementação: specs/006-check-balance/plan.md
- Checklist de tarefas: specs/006-check-balance/tasks.md
