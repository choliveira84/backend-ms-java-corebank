# Requisitos Funcionais e Não Funcionais - CoreBank

## 1. Premissas de Sistema
* **Autenticação Prévia:** O sistema assume que as chamadas originadas do aplicativo cliente já contemplam um contexto de segurança estabelecido. O identificador da conta (`accountId`) será extraído diretamente da requisição autenticada, simplificando a fronteira de entrada.

## 2. Requisitos Funcionais (RF)
* **RF01 - Autorização de Transação de Débito:** O sistema deve autorizar e processar compras por cartão e transferências (ex: PIX).
* **RF02 - Rejeição por Saldo Insuficiente:** O sistema deve rejeitar instantaneamente qualquer tentativa de transação se o saldo consolidado, derivado do Event Store, não for suficiente.
* **RF03 - Consulta Rápida de Saldo:** O sistema deve fornecer o saldo atualizado da conta do cliente de forma imediata quando solicitado pela interface, utilizando a base de leitura otimizada.
* **RF04 - Geração de Eventos de Domínio:** Cada transação bem-sucedida deve disparar um evento correspondente (ex: `TransactionAuthorizedEvent`) para compor o histórico imutável.

## 3. Requisitos Não Funcionais (RNF)
* **RNF01 - Integridade Transacional (ACID):** A autorização de transações (RF01 e RF02) precisa ser **absolutamente precisa**. O sistema não pode aprovar valores além do saldo real, nem negar transações incorretamente, requerendo consistência forte no momento da validação do *Command* no PostgreSQL.
* **RNF02 - Tolerância de Latência de Leitura (Eventual Consistency):** O saldo exibido (RF03) pode ter **até 5 segundos de defasagem** em relação ao momento em que uma transação de escrita foi efetuada.
* **RNF03 - Isolamento de Carga de Banco de Dados:** A arquitetura deve garantir que as consultas de saldo (historicamente 90% da carga) não disputem CPU ou I/O de disco com o banco primário responsável pelas autorizações.
* **RNF04 - Tempo de Resposta de Consulta:** A chamada para obter o saldo do usuário deve ocorrer no menor tempo possível, viabilizada pela estrutura em memória (Redis).
* **RNF05 - Escalabilidade Independente:** Os componentes e bancos de dados que servem leitura e escrita devem poder ser escalados horizontalmente de forma isolada.
* **RNF06 - Portabilidade e Implantação (Containerização):** Todo o ecossistema da aplicação (serviço backend, PostgreSQL, Redis e RabbitMQ) deve ser obrigatoriamente empacotado em contêineres Docker, garantindo isolamento de recursos, paridade entre ambientes e facilidade de implantação em esteiras de CI/CD.

## 4. Pendências e Itens Faltantes para Alinhamento ao Manifesto

A implementação atual cobre o MVP funcional da aplicação. Para evoluir de um protótipo válido para uma solução mais próxima do manifesto arquitetural, os pontos a seguir ainda precisam ser consolidados:

* **P01 - Idempotência de transação:** Garantir que a mesma autorização não seja processada mais de uma vez em caso de retry, timeout ou reprocessamento do cliente.
* **P02 - Payload de evento enriquecido:** O evento publicado via outbox deve incluir metadados completos e consistentes, como identificador da transação, conta, tipo, valor, timestamp e status final.
* **P03 - Reprocessamento seguro do outbox:** Definir política de retry, dead-letter queue ou reprocessamento controlado para eventos que falharem durante a publicação para RabbitMQ.
* **P04 - Consistência eventual validada em teste:** **Implementado nesta feature.** Testes automatizados validam a janela de 5 segundos, a resposta imediata com a última projeção disponível e a sinalização de eventos obsoletos.
* **P05 - Projeção de saldo mais determinística:** **Parcialmente implementado nesta feature.** O cálculo agora usa o saldo projetado existente, registra o último transactionId aplicado e ignora eventos duplicados. A reconstrução completa a partir do histórico permanece no P06.
* **P06 - Reconstituição do estado por eventos:** Avançar do modelo atual para um fluxo mais próximo de Event Sourcing, em que o estado da conta possa ser reconstruído a partir do histórico de eventos.
* **P07 - Observabilidade operacional:** Adicionar logs estruturados, métricas de negócio/infraestrutura, health checks e tracing para suporte a produção.
* **P08 - Autenticação e autorização real:** O header `X-Account-Id` representa um contexto autenticado assumido pela camada de entrada. Para produção, esse comportamento deve evoluir para autenticação real, com validação de identidade e autorização por papel/escopo.
* **P09 - Cobertura de integrações reais:** Validar, em ambiente mais próximo da produção, fluxos com PostgreSQL, Redis e RabbitMQ trabalhando em conjunto, incluindo falhas temporárias, filas e sincronização de leitura.
* **P10 - Testes de carga e tempo de resposta:** Verificar experimentalmente os tempos de resposta da consulta de saldo e da autorização em cenários com volume realista, garantindo que o RNF04 e o RNF05 sejam atendidos em prática.

## 5. Status Atual do Projeto

O projeto já está em um estado de MVP funcional, cobrindo os requisitos essenciais de autorização, saldo e testes de API. A principal lacuna enceguece no nível da maturidade operacional e arquitetural: a solução atende ao comportamento esperado do domínio, mas ainda precisa de reforços para aproximar-se da visão completa do manifesto e de um ambiente de produção mais resiliente.