## Visão geral do projeto

Esse projeto é um MVP bancário em Java/Spring Boot pensado para provar a ideia de arquitetura orientada a performance e a consistência de leitura, com foco em saldo de conta e autorização de transações. A base conceitual está bem definida em `README.md`, `manifesto_arquitetural_corebank.md` e `requisitos_corebank.md`.

O ponto central é simples e forte de apresentar em entrevista:

- a escrita acontece em PostgreSQL, como fonte de verdade transacional;
- a leitura de saldo usa Redis, como projeção otimizada;
- a propagação das mudanças ocorre via RabbitMQ;
- as regras de negócio ficam no domain core, sem acoplamento direto à infraestrutura.

---

## Arquitetura e fluxo principal

### 1) Camada de API
Os endpoints expõem a funcionalidade de negócio diretamente:

- `AccountBalanceController.java`
- `TransactionController.java`

A API é mínima e focada:
- `GET /api/v1/accounts/balance`
- `POST /api/v1/transactions/authorize`

Essa simplicidade é positiva em entrevista porque mostra que a camada externa não está carregada de lógica; ela apenas orquestra.

### 2) Caso de uso e regra de negócio
A lógica principal está em:

- `AuthorizeTransactionUseCaseImpl.java`
- `GetAccountBalanceQueryImpl.java`

O fluxo de autorização faz o seguinte:

- busca a conta no ledger do PostgreSQL;
- valida saldo disponível;
- subtrai o valor do saldo;
- grava a atualização no ledger;
- salva o histórico da transação;
- grava um evento no outbox;
- retorna sucesso.

Isso é uma escolha muito boa para mostrar domínio, transação e consistência forte no ponto de escrita.

### 3) Domínio
Os tipos principais do domínio estão em:

- `AccountLedger.java`
- `BalanceProjection.java`
- `TransactionType.java`

A ideia é clara:
- `AccountLedger` representa o saldo real no banco de escrituras;
- `BalanceProjection` representa a leitura rápida em Redis;
- `TransactionType` limita os tipos válidos (`DEBIT`, `TRANSFER`, `PIX`).

---

## O que é realmente interessante no projeto

### CQRS físico e desacoplamento de leitura/escrita
A arquitetura busca separar claramente o caminho de escrita do caminho de leitura. Isso é uma decisão muito valorizada em entrevistas, porque mostra consciência de escala e de carga.

A principal base dessa ideia está no projeto e no manifesto:
- `manifesto_arquitetural_corebank.md`
- `README.md`

Em termos práticos:
- escrita: PostgreSQL
- leitura: Redis
- evento: RabbitMQ

### Outbox pattern
Esse projeto usa outbox para não perder eventos no processo de autorização:

- `OutboxEventPublisher.java`

O processo é:

- a transação de negócio grava evento no outbox;
- um agendador publica para RabbitMQ;
- a fila alimenta a atualização da projeção.

Essa é uma decisão arquitetural madura e muito bem vista porque reduz risco de inconsistência entre banco e mensageria.

### Projeção de saldo e consistência eventual
A atualização do saldo em Redis acontece em:

- `BalanceProjectionUpdater.java`

Esse componente:
- lê o evento da fila;
- verifica se o saldo existe;
- ignora eventos duplicados usando `lastAppliedTransactionId`;
- recalcula a projeção;
- salva no Redis.

O projeto também aplica política de frescor de projeção com janela de 5 segundos, observada em `ProjectionConsistencyPolicy.java`. Isso mostra alinhamento com o requisito de consistência eventual.

---

## Pontos fortes para apresentar

### 1) Arquitetura pensada para escala
Você consegue dizer que o projeto não foi construído como um monólito clássico “um banco para tudo”. Ele separa:
- responsabilidade de escrita;
- responsabilidade de leitura;
- propagação assíncrona;
- leitura rápida.

### 2) Use case real e aplicado
O caso de uso de transação com saldo é concreto, fácil de explicar e muito palpável. Em entrevista, você consegue contar a jornada:
- conta existe?
- há saldo suficiente?
- escreve no ledger;
- grava no histórico;
- envia evento;
- atualiza projeção.

### 3) Visão de engenharia e não só “CRUD”
O projeto demonstra:
- domínio isolado;
- ports/adapters;
- desacoplamento;
- uso de filas;
- leitura otimizada;
- documentação de API.

Isso é um diferencial grande.

---

## Limitações honestas (e que podem aparecer em pergunta)

Esse é o ponto mais importante: o projeto é bom, mas ainda é um MVP.

### 1) Não é um Event Sourcing completo
O manifesto fala em Event Sourcing, mas a implementação ainda é mais um modelo de:
- transação + outbox + projeção
- não reconstrução total do estado a partir do histórico.

Isso foi reconhecido no próprio documento de requisitos em `requisitos_corebank.md`. Em entrevista, você pode dizer isso com maturidade:
> “O projeto atende ao MVP funcional de saldo e autorização, mas ainda está em um estágio intermediário entre protótipo e arquitetura totalmente orientada a eventos.”

### 2) Autenticação simplificada
O sistema assume header `X-Account-Id` como contexto autenticado. Isso é um trade-off válido para MVP, mas não é solução de produção. Isso aparece em `requisitos_corebank.md` como pendência de autenticação real.

### 3) Idempotência e retry ainda precisam ser reforçados
O projeto já tenta lidar com duplicidade de evento, mas a idempotência de transação não está totalmente robusta como em sistemas de produção. Isso é um ponto crítico para falar com segurança e mostrar que você entende a diferença entre “funciona em laboratório” e “resiste a retrys e falhas reais”.

### 4) Observabilidade e produção
Falta reforço em:
- logs estruturados;
- métricas;
- health checks;
- tracing;
- política de retry/dead letter para RabbitMQ.

Esses pontos saem no próprio documento de pendências.

---

## Como eu apresentaria esse projeto em 1 minuto

> “Este projeto é um backend bancário em Java com Spring Boot, desenhado com arquitetura hexagonal e visão de CQRS físico. A conta usa PostgreSQL como source of truth para as transações e o Redis para leitura rápida do saldo; as alterações são propagadas via RabbitMQ usando outbox para manter consistência com desacoplamento. O objetivo era resolver o clássico problema de leitura intensa x escrita crítica. O projeto funciona como um MVP funcional e mostra maturidade arquitetural, mas ainda tem gaps de produção, como autenticação real, idempotência, observabilidade e refinamento do modelo de eventos.”

---

## O que dizer se te perguntarem “qual foi o maior aprendizado?”

Você pode responder assim:

> “O maior aprendizado foi equilibrar negócio, performance e consistência. O sistema precisa ser correto na escrita e rápido na leitura, mas sem transformar a arquitetura em algo complexo demais para um MVP. O projeto mostrou que é possível separar o modelo operacional da projeção de consulta e ainda manter um fluxo de negócio coerente.”

---

## O que dizer se te perguntarem “qual é o ponto fraco?”

> “O principal ponto fraco é que ele ainda não está na maturidade de um ambiente de produção completo. Ele cumpre o comportamento funcional esperado, mas ainda precisa evoluir em autenticação real, idempotência, reprocessamento de eventos e observabilidade.”

---

## Minha recomendação para a entrevista

Se você for questionado, apresente assim:

1. “O objetivo do projeto”
2. “A arquitetura escolhida”
3. “O fluxo de transação e saldo”
4. “Por que essa arquitetura é adequada”
5. “O que ainda falta para produção”

Isso mostra:
- domínio;
- visão técnica;
- consciência crítica;
- maturidade.

Se quiser, na próxima mensagem eu posso te preparar um roteiro de perguntas e respostas estilo entrevista para esse projeto, com 20 perguntas comuns e respostas prontas para você memorizar.