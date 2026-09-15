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