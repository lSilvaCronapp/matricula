# Gestão de Matrículas Acadêmicas

Solução para cadastro de alunos, cursos, disciplinas e turmas, com controle de matrículas e regras de negócio.

> Planejamento completo: [`docs/DECISOES-TECNICAS.md`](docs/DECISOES-TECNICAS.md)

## Status

| Fase | Descrição | Status |
|------|-----------|--------|
| 0 | Esqueleto do repositório + Postgres | Concluída — [`docs/FASE-0-EXECUCAO.md`](docs/FASE-0-EXECUCAO.md) |
| 1 | Backend (API REST) | Concluída — [`docs/FASE-1-EXECUCAO.md`](docs/FASE-1-EXECUCAO.md) |
| 2 | Frontend (Angular) | Concluída — [`docs/FASE-2-EXECUCAO.md`](docs/FASE-2-EXECUCAO.md) |
| 3 | Docker completo + README final | Pendente |

## Stack (planejada)

- **Backend:** Java 21, Spring Boot 3.x, Maven, Spring Data JPA, Bean Validation, Springdoc OpenAPI
- **Frontend:** Angular (standalone), TypeScript, Angular Material
- **Banco:** PostgreSQL 16
- **Ambiente:** Docker Compose

## Estrutura do monorepo

```text
gestao-matricula/
├── backend/           # Spring Boot (pacote br.edu.com.matricula)
├── frontend/          # Angular (Fase 2)
├── docs/              # Decisões e relatórios de fase
├── docker-compose.yml
├── .env.example
└── README.md
```

## Pré-requisitos

- Docker e Docker Compose
- (Fases seguintes) JDK 21, Maven Wrapper, Node.js / Angular CLI

## Como executar (Fase 0)

Sobe **apenas o banco** de dados:

```bash
cp .env.example .env   # opcional
docker compose up -d db
```

Verificar saúde:

```bash
docker compose ps
docker compose exec db pg_isready -U matricula -d matricula
```

Conexão JDBC local (backend na Fase 1):

```text
jdbc:postgresql://localhost:5432/matricula
usuário: matricula
senha: matricula
```

Encerrar:

```bash
docker compose down
```

Apagar dados do volume:

```bash
docker compose down -v
```

## Acessos (quando as demais fases existirem)

| Serviço | URL |
|---------|-----|
| Frontend | http://localhost:4200 |
| API | http://localhost:8080/api/v1 |
| Swagger | http://localhost:8080/swagger-ui.html |

## Documentação

- [`docs/DECISOES-TECNICAS.md`](docs/DECISOES-TECNICAS.md) — decisões e fases
- Relatórios de execução por fase em `docs/`

## Backlog (fora do MVP)

Testes automatizados, migrations (Flyway/Liquibase), mensageria e documentação expandida.
