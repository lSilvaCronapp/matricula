# Gestão de Matrículas Acadêmicas

Solução para cadastro de alunos, cursos, disciplinas e turmas, com controle de matrículas e regras de negócio (criar, confirmar e cancelar).

> Planejamento completo: [`docs/DECISOES-TECNICAS.md`](docs/DECISOES-TECNICAS.md)

## Visão geral

Monorepo com API REST (Spring Boot), interface Angular e PostgreSQL. O fluxo principal é a matrícula de um aluno em uma turma: a solicitação nasce **PENDENTE** (sem consumir vaga), pode ser **CONFIRMADA** (consome vaga) ou **CANCELADA**.

## Stack

| Camada | Tecnologia |
|--------|------------|
| Backend | Java 21, Spring Boot 3.5, Maven, Spring Data JPA, Bean Validation, Springdoc OpenAPI |
| Frontend | Angular 19 (standalone), TypeScript, Reactive Forms, Angular Material |
| Banco | PostgreSQL 16 |
| Ambiente | Docker Compose (`db`, `backend`, `frontend`) |

## Pré-requisitos

- Docker e Docker Compose
- (opcional, desenvolvimento local) JDK 21 + Maven, Node.js 20+

## Como executar

### Docker Compose (recomendado)

```bash
cp .env.example .env   # opcional
docker compose up --build
```

Aguarde o healthcheck do Postgres e a subida do backend. Na primeira vez o build pode demorar (Maven + npm).

Encerrar:

```bash
docker compose down
```

Apagar dados do volume Postgres:

```bash
docker compose down -v
```

> `docker compose down -v` remove o volume `pgdata` e **apaga** os dados do banco.

### Desenvolvimento local (db no Docker + apps locais)

```bash
docker compose up -d db
```

**Backend** (porta 8080):

```bash
cd backend
# com Maven local ou via container:
docker run --rm -v "$PWD":/workspace -w /workspace --network host \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/matricula \
  -e SPRING_DATASOURCE_USERNAME=matricula \
  -e SPRING_DATASOURCE_PASSWORD=matricula \
  maven:3.9.10-eclipse-temurin-21 \
  mvn spring-boot:run
```

**Frontend** (porta 4200, proxy `/api` → `localhost:8080`):

```bash
cd frontend
npm install
npm start
```

## Acessos

| Serviço | URL |
|---------|-----|
| Frontend | http://localhost:4200 |
| API | http://localhost:8080/api/v1 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/api-docs |

No modo Docker, o browser deve usar o frontend em `:4200`; as chamadas `/api` são encaminhadas pelo nginx ao backend. Não chame o hostname Docker `backend` a partir do navegador.

## Principais decisões técnicas

- Camadas: `controller` → `service` → `domain` / `repository`, com `dto` + `mapper`
- IDs **UUID**; aluno com **CPF** e **matrícula acadêmica (RA)**
- Matrícula nasce `PENDENTE`; vaga só é consumida na **confirmação**
- Unique `(aluno_id, turma_id)` — cancelada **não** permite remarcar a mesma turma
- Exclusão bloqueada quando há vínculo relevante (HTTP **409**)
- Optimistic lock (`@Version`) na entidade `Turma`
- Erros padronizados (`ErrorResponse`); regras de negócio em **409**
- Timestamps em **UTC** no backend/banco, ISO na API, exibição em **America/Sao_Paulo** / `pt-BR` no Angular
- Proxy `/api` (nginx em produção; `proxy.conf.json` no `ng serve`)
- DDL no MVP: `spring.jpa.hibernate.ddl-auto=update` (migrations no backlog)

## Modelo de domínio e regras de negócio

```text
Curso 1 ── N Disciplina
Disciplina 1 ── N Turma
Aluno 1 ── N Matricula
Turma 1 ── N Matricula
```

Matrícula é sempre na **turma**.

**Status da turma:** `ABERTA` | `FECHADA`  
**Status da matrícula:** `PENDENTE` | `CONFIRMADA` | `CANCELADA`

Transições:

```text
criar → PENDENTE → confirmar → CONFIRMADA
              └→ cancelar → CANCELADA
CONFIRMADA → cancelar → CANCELADA
```

- Criar como `PENDENTE` **não** altera `vagasOcupadas`
- Confirmar exige turma `ABERTA` e vaga disponível (`vagasOcupadas < limiteVagas`) → +1 vaga
- Cancelar `CONFIRMADA` → −1 vaga; cancelar `PENDENTE` não altera vagas
- Turma `FECHADA` impede novas matrículas/confirmações

## Endpoints principais

Base: `/api/v1`

CRUD: `alunos`, `cursos`, `disciplinas`, `turmas`  
(`POST`, `GET`, `GET/{id}`, `PUT/{id}`, `DELETE/{id}`)

Filtro útil: `GET /turmas?status=ABERTA`

| Método | Path | Efeito |
|--------|------|--------|
| `POST` | `/matriculas` | cria `PENDENTE` |
| `GET` | `/matriculas/{id}` | detalhe |
| `PATCH` | `/matriculas/{id}/confirmar` | confirma e consome vaga |
| `PATCH` | `/matriculas/{id}/cancelar` | cancela e libera vaga se preciso |
| `GET` | `/matriculas/aluno/{alunoId}` | por aluno |
| `GET` | `/matriculas/turma/{turmaId}` | por turma |

## Tratamento de erros

| HTTP | Caso |
|------|------|
| 400 | Validação de entrada (`details` por campo) |
| 404 | Recurso não encontrado |
| 409 | Regra de negócio / conflito / exclusão com vínculo |
| 500 | Erro inesperado |

Formato:

```json
{
  "timestamp": "2026-07-16T20:00:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Turma sem vagas disponíveis",
  "path": "/api/v1/matriculas/{id}/confirmar",
  "details": []
}
```

## Limitações do MVP

- Sem autenticação / autorização
- Sem migrations versionadas (`ddl-auto: update`)
- Sem testes automatizados de domínio
- Sem mensageria
- Matrícula `CANCELADA` não volta a `PENDENTE`/`CONFIRMADA`

## Backlog

- Testes do `MatriculaService` (primeiro item se sobrar tempo)
- Flyway / Liquibase
- Mensageria
- Documentação além do README + Swagger

## Estrutura do monorepo

```text
gestao-matricula/
├── backend/           # Spring Boot (br.edu.com.matricula)
├── frontend/          # Angular standalone + Material
├── docs/              # decisões e relatórios de fase (local)
├── docker-compose.yml
├── .env.example
└── README.md
```

## Checklist de demo

1. Cadastrar curso → disciplina → turma (`ABERTA`, limite de vagas)
2. Cadastrar aluno (CPF + RA)
3. Criar matrícula → `PENDENTE` → `vagasOcupadas` **não** muda
4. Confirmar → `CONFIRMADA` → `vagasOcupadas` +1
5. Segunda matrícula mesma turma → **409**
6. Cancelar confirmada → vaga liberada
7. Fechar turma → nova matrícula/confirmação bloqueadas
8. Consultar por aluno e por turma
9. Excluir curso com disciplina → **409**

## Documentação adicional

- [`docs/DECISOES-TECNICAS.md`](docs/DECISOES-TECNICAS.md)
- Relatórios: `docs/FASE-0-EXECUCAO.md` … `docs/FASE-3-EXECUCAO.md`
