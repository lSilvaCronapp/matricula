# Relatório de Execução — Fase 0

| Campo | Valor |
|-------|-------|
| Fase | **0 — Esqueleto do repositório** |
| Status | **Concluída** (aguardando validação humana) |
| Data | 2026-07-16 |
| Documento de decisões | [`DECISOES-TECNICAS.md`](./DECISOES-TECNICAS.md) |

---

## Objetivo da fase

1. Criar o monorepo com `backend/`, `frontend/`, `docker-compose.yml`, `.env.example` e README inicial  
2. Subir **somente** o PostgreSQL via Docker Compose e validar a conexão  
3. **Não** implementar domínio (entidades, CRUD, telas) além do necessário para o esqueleto  

---

## O que foi executado

### 1. Estrutura do monorepo

Criados/atualizados:

```text
gestao-matricula/
├── backend/
│   ├── pom.xml
│   └── src/main/java/br/edu/com/matricula/
│       ├── MatriculaApplication.java
│       ├── domain/{model,enums}/
│       ├── repository/
│       ├── dto/{request,response}/
│       ├── mapper/
│       ├── service/
│       ├── controller/
│       ├── exception/
│       └── config/
├── frontend/
│   └── README.md                  # placeholder até a Fase 2
├── docs/
│   ├── DECISOES-TECNICAS.md
│   └── FASE-0-EXECUCAO.md         # este arquivo
├── docker-compose.yml             # serviço db apenas
├── .env.example
├── .env                           # cópia local a partir do example (não versionar segredos em produção)
├── .gitignore
└── README.md
```

Pacotes Java vazios marcados com `.gitkeep` para preservar a árvore alinhada às decisões técnicas.

### 2. Backend (esqueleto)

| Item | Detalhe |
|------|---------|
| Build | Maven (`pom.xml`) |
| Spring Boot | **3.5.3** (linha 3.x do planejamento) |
| Java | 21 (propriedade no POM) |
| Pacote base | `br.edu.com.matricula` |
| Dependências declaradas | web, data-jpa, validation, postgresql, springdoc-openapi-starter-webmvc-ui (2.8.9), test |
| Config | `application.yml` com datasource via env, `ddl-auto=update`, timezone Jackson/Hibernate **UTC** |
| Domínio | **não** implementado (sem entidades/controllers) |

**Nota:** o Spring Initializr (`start.spring.io`) em 2026-07-16 só oferecia Spring Boot **≥ 4.0**. Para respeitar a decisão “Spring Boot 3.x”, o esqueleto foi criado **manualmente** com parent `3.5.3`.

**Ambiente local observado:** JDK 11 e ausência de Maven/Node no PATH. A Fase 0 **não** exigiu compilar o backend; a compilação/execução da API fica para a Fase 1 (com JDK 21).

### 3. Frontend (placeholder)

- Diretório `frontend/` criado com `README.md` descrevendo o que será feito na Fase 2  
- Projeto Angular **ainda não** gerado (sem Node/Angular CLI no ambiente; geração na Fase 2)

### 4. Docker Compose

- Serviço `db`: imagem `postgres:16`
- Variáveis: `POSTGRES_DB/USER/PASSWORD=matricula`
- Porta host: `5432`
- Volume: `pgdata`
- Network: `matricula-net`
- Healthcheck: `pg_isready`
- Timezone do container: `UTC` / `PGTZ=UTC`
- Serviços `backend` e `frontend`: **não** incluídos (Fase 3)

### 5. Validação do banco

Comandos executados:

```bash
docker compose up -d db
docker compose exec db pg_isready -U matricula -d matricula
docker compose exec db psql -U matricula -d matricula -c "SELECT ..."
```

Resultado:

| Verificação | Resultado |
|-------------|-----------|
| Container | `gestao-matricula-db` — **Up (healthy)** |
| `pg_isready` | accepting connections |
| Database / user | `matricula` / `matricula` |
| Versão | PostgreSQL **16.14** |
| `TimeZone` | **UTC** |
| `server_encoding` | **UTF8** |

JDBC local previsto para a Fase 1:

```text
jdbc:postgresql://localhost:5432/matricula
```

---

## Fora do escopo desta fase (correto)

- Entidades, repositórios, services, controllers  
- Regras de matrícula  
- Angular / Material / telas  
- Dockerfiles de backend e frontend  
- Testes de domínio (backlog)  
- Migrations (backlog)  

---

## Critérios da Fase 0 × status

| Critério (DECISOES-TECNICAS §6.2) | Status |
|-----------------------------------|--------|
| Criar `backend/`, `frontend/`, `docker-compose.yml`, `.env.example`, README inicial | Concluído |
| Subir só Postgres via Compose e validar conexão | Concluído |
| Não implementar domínio além do esqueleto | Concluído |

---

## Próximo passo

Após validação humana deste relatório e do trecho atualizado em `DECISOES-TECNICAS.md`:

→ **Fase 1 — Backend** (API REST completa conforme decisões).

---

## Como reproduzir a Fase 0

```bash
cp .env.example .env   # opcional
docker compose up -d db
docker compose ps
docker compose exec db pg_isready -U matricula -d matricula
```
