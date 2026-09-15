# Infrastructure Contracts

This document describes the external interfaces exposed by the Dockerized environment.

## Local Development Ports

| Service | Internal Port | Host Mapped Port | Credentials (Dev) |
|---------|---------------|------------------|-------------------|
| PostgreSQL | 5432 | 5432 | `postgres` / `postgres` |
| Redis | 6379 | 6379 | No password |
| RabbitMQ | 5672 | 5672 | `guest` / `guest` |
| RabbitMQ (UI)| 15672 | 15672 | `guest` / `guest` |

*Note: The application will connect to these host ports during local execution.*
