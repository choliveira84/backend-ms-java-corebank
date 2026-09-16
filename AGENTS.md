# AGENTS.md

## Visão geral

Este repositório é um backend Java 21 com Spring Boot 3.x para um sistema bancário seguindo arquitetura hexagonal, CQRS e consistência eventual. O código-fonte principal fica em `src/main/java/com/corebank` e a suíte de testes em `src/test/java`.

## Comandos de build e validação

- Iniciar infraestrutura local (PostgreSQL, Redis, RabbitMQ): `docker compose up -d`
- Rodar a aplicação localmente: `./mvnw spring-boot:run`
- Executar a suíte de testes: `./mvnw test`
- Rodar um teste específico: `./mvnw -Dtest=NomeDoTeste test`

## Estrutura de código

- `domain/`: regras de negócio, entidades, value objects, exceções e contratos de port
- `application/`: casos de uso, consultas e eventos de aplicação
- `infrastructure/`: adapters, configuração Spring, controllers, persistência JPA/Redis/RabbitMQ
- `specs/`: fonte de verdade para requisitos e evolução de features. Sempre consulte o diretório relevante antes de alterar comportamento de negócio

## Regras de edição

- Mantenha domain/application limpos e orientados a negócio; controllers e infraestrutura não devem conter lógica de regra de negócio
- Quando uma mudança alterar contrato, comportamento ou regra, verifique a spec correspondente em `specs/<id>/`
- Preserve o padrão de nomes do projeto: pacotes em `com.corebank.*`; nomes de casos de uso terminando em `UseCase`/`UseCaseImpl` e queries por `Query`/`QueryImpl`
- Prefira evitar duplicação e mantenha testes próximos do comportamento alterado

## Convenções do projeto

- O projeto usa Springdoc OpenAPI; a documentação fica em `/swagger-ui/index.html` quando a app está rodando
- O fluxo de leitura de saldo é eventual e depende de projeção em Redis; não trate isso como inconsistencia imediata em testes de integração
- A criação de conta de teste em ambiente local está exposta em endpoints de dev/test para facilitar validação manual
- O sistema trabalha com eventos de outbox e projeção assíncrona; mudanças relacionadas a transações devem manter esse contrato em mente

## Pitfalls comuns

- Não insira lógica de domínio em `infrastructure/web` ou `config`
- Não altere comportamento de validação sem verificar as classes de exception e regras de negócio correspondentes
- Antes de alterar endpoints, confirme se o endpoint em questão já foi documentado na spec e no README
- Ao tocar em Redis/RabbitMQ/PostgreSQL, preserve compatibilidade com o fluxo de consistência eventual

## Referências úteis

- [README.md](README.md)
- [pom.xml](pom.xml)
- [specs](specs)

Use este guia como fonte rápida de contexto para qualquer mudança no projeto e mantenha a implementação alinhada com a arquitetura e a documentação existente.
