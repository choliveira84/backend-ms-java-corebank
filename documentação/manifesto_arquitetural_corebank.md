# Visão Geral e Manifesto Arquitetural - CoreBank

## 1. Visão Geral
O CoreBank enfrenta um desafio crítico de escalabilidade estrutural. O modelo atual baseia-se em um banco de dados relacional monolítico onde as operações de leitura (consulta de saldo, representando 90% da carga) asfixiam as operações críticas de escrita (transações e autorizações, 10%). A arquitetura proposta visa eliminar esse gargalo através da separação física de responsabilidades, garantindo alta disponibilidade, performance extrema nas consultas e integridade transacional absoluta.

## 2. Princípios Arquiteturais (Manifesto)
Para suportar a volumetria de 2 milhões de contas ativas e preparar o ecossistema para o futuro, adotaremos as seguintes diretrizes fundacionais:

* **Arquitetura Hexagonal (Ports and Adapters):** O *core domain* (domínio de negócio) será o centro da aplicação, completamente agnóstico de infraestrutura, banco de dados ou frameworks externos.
* **CQRS (Command Query Responsibility Segregation) Físico:** Implementaremos a persistência poliglota. O lado da escrita será isolado do lado da leitura, garantindo escala independente.
* **Event Sourcing:** A verdade absoluta do sistema não é o saldo consolidado, mas sim o fluxo imutável de eventos passados. O estado atual será sempre uma projeção (fold) desse histórico.
* **Imutabilidade e Design Limpo (Zero Boilerplate Externo):** Utilizaremos `records` nativos para modelar Commands, Events e DTOs, garantindo imutabilidade por design. Fica expressamente vetado o uso de bibliotecas de injeção de código para modelagem de dados, mantendo o código puro e previsível. Classes padrão serão reservadas estritamente para os *Aggregate Roots*, onde a gestão de estado complexo é necessária.
* **Consistência Eventual Tolerante:** Aceitaremos um atraso aceitável (delay máximo de 5 segundos) na sincronização das bases de leitura, utilizando mensageria assíncrona para garantir o desacoplamento.

## 3. Stack Tecnológico Sugerido
* **Ecossistema Backend:** Java 17 ou 21 utilizando o framework Spring Boot.
* **Write Data Store (Event Store):** PostgreSQL. Aproveitaremos os recursos de colunas `JSONB` para armazenamento rápido, flexível e tipado dos payloads de eventos de domínio, garantindo operações velozes de *append-only*.
* **Read Data Store (Cache/Projeção):** Redis, oferecendo tempo de resposta de complexidade O(1) para a consulta de saldo dos clientes.
* **Mensageria/Broker:** RabbitMQ para propagação assíncrona dos eventos de domínio do lado da escrita para a atualização do modelo de leitura.
* **Mapeamento de Objetos:** MapStruct para a transição fluida de entidades entre as portas e os adaptadores.
* **Containerização e Orquestração Local:** Docker para o empacotamento da aplicação, contando com Docker Compose para subir ambientes locais conteinerizados de forma ágil, orquestrando perfeitamente todos os nossos contêineres de infraestrutura (PostgreSQL, Redis e RabbitMQ).