# Decisões Técnicas — Gestão de Matrículas Acadêmicas

Documento de referência para implementação. Qualquer agente ou desenvolvedor deve seguir este arquivo como fonte de verdade do planejamento.

| Campo | Valor |
|-------|-------|
| Nome do projeto / repositório | `gestao-matricula` |
| Pacote Java base | `br.edu.com.matricula` |
| Tipo de repositório | Monorepo (`backend/` + `frontend/` + `docker-compose.yml`) |
| Status do planejamento | Fechado e validado |
| Fase atual | **Fase 0 concluída** (aguardando validação do relatório) → próxima: **Fase 1** |

### Relatórios de execução por fase

| Fase | Status | Relatório |
|------|--------|-----------|
| 0 — Esqueleto | Concluída (pendente validação humana) | [`FASE-0-EXECUCAO.md`](./FASE-0-EXECUCAO.md) |
| 1 — Backend | Pendente | — |
| 2 — Frontend | Pendente | — |
| 3 — Docker e entrega | Pendente | — |

---

## Sumário

1. [Escopo do MVP e backlog](#1-escopo-do-mvp-e-backlog)
2. [Tópico 1 — Domínio e regras de negócio](#2-tópico-1--domínio-e-regras-de-negócio)
3. [Tópico 2 — Arquitetura do backend](#3-tópico-2--arquitetura-do-backend)
4. [Tópico 3 — Arquitetura do frontend](#4-tópico-3--arquitetura-do-frontend)
5. [Tópico 4 — Banco, Docker e ambiente](#5-tópico-4--banco-docker-e-ambiente)
6. [Tópico 5 — Fases de implementação e entrega](#6-tópico-5--fases-de-implementação-e-entrega)
7. [Checklist de demo](#7-checklist-de-demo)
8. [README de entrega — estrutura obrigatória](#8-readme-de-entrega--estrutura-obrigatória)
9. [Instruções para agentes](#9-instruções-para-agentes)
10. [Andamento das fases](#10-andamento-das-fases)

---

## 1. Escopo do MVP e backlog

### 1.1 Entra no MVP

- CRUD completo de Aluno, Curso, Disciplina e Turma
- Matrícula de aluno em turma (criar, confirmar, cancelar)
- Consulta de matrículas por aluno e por turma
- Validação de entrada nas APIs
- Tratamento padronizado de erros
- Frontend Angular com telas separadas, interceptor e consumo tipado da API
- PostgreSQL via Docker Compose
- Serviços Compose: `db`, `backend`, `frontend`
- Springdoc OpenAPI (Swagger) no MVP
- README completo com instruções e decisões técnicas

### 1.2 Fica no backlog (não implementar no MVP)

- Testes automatizados (primeiro item a puxar se sobrar tempo: testes do `MatriculaService`)
- Migrations (Flyway/Liquibase) — no MVP usar `ddl-auto: update`
- Mensageria
- Docker com mensageria
- Documentação além do README + Swagger

---

## 2. Tópico 1 — Domínio e regras de negócio

### 2.1 Decisões confirmadas

| # | Decisão |
|---|--------|
| IDs | **UUID** |
| Aluno | possui **CPF** e **matriculaAcademica** (RA) |
| Fluxo de matrícula | nasce **PENDENTE**; depois confirma ou cancela |
| Unicidade | unique `(aluno_id, turma_id)` sempre — matrícula cancelada **não** permite remarcar a mesma turma |
| Exclusão | **bloqueada** se houver vínculo relevante |
| Turma fechada | só impede novas matrículas/confirmações; matrículas já confirmadas permanecem |
| Turma | inclui campo **`ano`** |

### 2.2 Enums

```text
StatusTurma:     ABERTA | FECHADA
StatusMatricula: PENDENTE | CONFIRMADA | CANCELADA
```

### 2.3 Entidades e campos

#### Aluno

| Campo | Tipo | Obrigatório | Observação |
|-------|------|-------------|------------|
| `id` | UUID | sim | PK |
| `nome` | String | sim | 2–120 caracteres |
| `email` | String | sim | único, formato e-mail |
| `cpf` | String | sim | único, 11 dígitos (somente números) |
| `matriculaAcademica` | String | sim | único (RA) |
| `dataNascimento` | LocalDate | não | se informado, não pode ser futura |
| `ativo` | Boolean | sim | default `true` |
| `createdAt` / `updatedAt` | Instant (UTC) | sim | auditoria |

#### Curso

| Campo | Tipo | Obrigatório | Observação |
|-------|------|-------------|------------|
| `id` | UUID | sim | PK |
| `nome` | String | sim | 2–120 caracteres |
| `codigo` | String | sim | único (ex.: `ENG-SOFT`) |
| `descricao` | String | não | até 500 caracteres |
| `ativo` | Boolean | sim | default `true` |
| `createdAt` / `updatedAt` | Instant (UTC) | sim | |

#### Disciplina

| Campo | Tipo | Obrigatório | Observação |
|-------|------|-------------|------------|
| `id` | UUID | sim | PK |
| `nome` | String | sim | |
| `codigo` | String | sim | único global |
| `cargaHoraria` | Integer | sim | `> 0` |
| `cursoId` | FK → Curso | sim | |
| `ativo` | Boolean | sim | default `true` |
| `createdAt` / `updatedAt` | Instant (UTC) | sim | |

#### Turma

| Campo | Tipo | Obrigatório | Observação |
|-------|------|-------------|------------|
| `id` | UUID | sim | PK |
| `codigo` | String | sim | único (ex.: `BD-2026-1A`) |
| `disciplinaId` | FK → Disciplina | sim | |
| `ano` | Integer | sim | ex.: `2026` |
| `periodo` | String | sim | `"1"` ou `"2"` (semestre); ano fica no campo `ano` |
| `limiteVagas` | Integer | sim | `>= 1` |
| `vagasOcupadas` | Integer | sim | default `0`; **somente backend** altera |
| `status` | StatusTurma | sim | `ABERTA` / `FECHADA` |
| `version` | Long | sim | optimistic lock (`@Version`) |
| `createdAt` / `updatedAt` | Instant (UTC) | sim | |

#### Matricula

| Campo | Tipo | Obrigatório | Observação |
|-------|------|-------------|------------|
| `id` | UUID | sim | PK |
| `alunoId` | FK → Aluno | sim | |
| `turmaId` | FK → Turma | sim | |
| `status` | StatusMatricula | sim | |
| `dataSolicitacao` | Instant (UTC) | sim | na criação |
| `dataConfirmacao` | Instant (UTC) | não | ao confirmar |
| `dataCancelamento` | Instant (UTC) | não | ao cancelar |
| `createdAt` / `updatedAt` | Instant (UTC) | sim | |

**Restrição de banco:** `UNIQUE (aluno_id, turma_id)`.

### 2.4 Relacionamentos

```text
Curso 1 ── N Disciplina
Disciplina 1 ── N Turma
Aluno 1 ── N Matricula
Turma 1 ── N Matricula
```

Matrícula é sempre na **turma** (não direto na disciplina).

### 2.5 Regras de negócio

#### Cadastros

1. `email`, `cpf` e `matriculaAcademica` de aluno únicos.
2. `codigo` de curso, disciplina e turma únicos.
3. Disciplina exige curso existente.
4. Turma exige disciplina existente.
5. `limiteVagas >= 1`.
6. Ao criar turma: `vagasOcupadas = 0`; status default `ABERTA` (cliente pode enviar `FECHADA`).
7. Atualizar `limiteVagas` só se `novoLimite >= vagasOcupadas`.
8. Exclusão bloqueada com vínculo:
   - Curso com disciplinas → 409
   - Disciplina com turmas → 409
   - Turma com matrículas → 409
   - Aluno com matrículas → 409

#### Matrícula — criação (`POST`)

1. Aluno e turma devem existir.
2. Aluno deve estar `ativo`.
3. Turma deve estar `ABERTA`.
4. Não pode existir outra matrícula do mesmo aluno na mesma turma (qualquer status).
5. Status inicial: `PENDENTE`.
6. Criar como `PENDENTE` **não** consome vaga.

#### Matrícula — confirmar

1. Matrícula existe e está `PENDENTE`.
2. Turma continua `ABERTA`.
3. Há vaga: `vagasOcupadas < limiteVagas`.
4. Status → `CONFIRMADA`; seta `dataConfirmacao`.
5. Incrementa `vagasOcupadas` em 1 (mesma transação).

#### Matrícula — cancelar

1. Matrícula existe.
2. Status permitido: `PENDENTE` ou `CONFIRMADA`.
3. Se era `CONFIRMADA`: decrementa `vagasOcupadas` em 1.
4. Se era `PENDENTE`: não altera vagas.
5. Status → `CANCELADA`; seta `dataCancelamento`.
6. Matrícula `CANCELADA` não volta a `PENDENTE`/`CONFIRMADA` no MVP.

#### Transições de status

```text
        criar
          │
          ▼
      PENDENTE ──confirmar──► CONFIRMADA
          │                      │
          └──cancelar──┐    cancelar
                       ▼         │
                    CANCELADA ◄──┘
```

| De | Para | Ação | Vagas |
|----|------|------|-------|
| — | `PENDENTE` | criar | sem mudança |
| `PENDENTE` | `CONFIRMADA` | confirmar | +1 ocupada |
| `PENDENTE` | `CANCELADA` | cancelar | sem mudança |
| `CONFIRMADA` | `CANCELADA` | cancelar | −1 ocupada |
| `CANCELADA` | * | — | proibido |
| `CONFIRMADA` | `PENDENTE` | — | proibido |

---

## 3. Tópico 2 — Arquitetura do backend

### 3.1 Decisões confirmadas

| # | Decisão |
|---|--------|
| Build | **Maven** |
| Base path | **`/api/v1`** |
| Conflito de negócio | HTTP **409** |
| Lock na turma | **`@Version`** (optimistic lock) |
| OpenAPI | **Springdoc** no MVP |
| Período | `ano` + `periodo` (`"1"` / `"2"`) |

### 3.2 Stack

| Item | Escolha |
|------|---------|
| Java | 21 |
| Spring Boot | 3.x |
| API | REST + JSON |
| Persistência | Spring Data JPA |
| Banco | PostgreSQL 16 |
| Validação | Bean Validation (`jakarta.validation`) |
| Docs API | Springdoc OpenAPI |

### 3.3 Estrutura de pacotes

```text
br.edu.com.matricula
├── MatriculaApplication
├── domain
│   ├── model          // entidades JPA
│   └── enums          // StatusTurma, StatusMatricula
├── repository
├── dto
│   ├── request
│   └── response
├── mapper
├── service
├── controller
├── exception          // exceções + GlobalExceptionHandler
└── config             // CORS, OpenAPI, etc.
```

| Camada | Faz | Não faz |
|--------|-----|---------|
| `controller` | HTTP, status code, chama service | regra de negócio, acesso a DB |
| `service` | regras, transações, orquestra repositórios | montar JSON HTTP |
| `domain/model` | entidades e enums | validação de API |
| `repository` | queries/CRUD JPA | regra de negócio |
| `dto` | contrato da API | lógica |
| `mapper` | conversão Entity ↔ DTO | regra |
| `exception` | padronizar erros | regra de domínio |

### 3.4 Endpoints

Base: `/api/v1`

#### CRUD (padrão para alunos, cursos, disciplinas, turmas)

- `POST /{recurso}` → 201
- `GET /{recurso}` → 200
- `GET /{recurso}/{id}` → 200
- `PUT /{recurso}/{id}` → 200
- `DELETE /{recurso}/{id}` → 204

Recursos: `alunos`, `cursos`, `disciplinas`, `turmas`.

#### Matrículas

| Método | Path | Efeito |
|--------|------|--------|
| `POST` | `/matriculas` | cria `PENDENTE` |
| `GET` | `/matriculas/{id}` | detalhe |
| `PATCH` | `/matriculas/{id}/confirmar` | confirma e consome vaga |
| `PATCH` | `/matriculas/{id}/cancelar` | cancela e libera vaga se preciso |
| `GET` | `/matriculas/aluno/{alunoId}` | por aluno |
| `GET` | `/matriculas/turma/{turmaId}` | por turma |

Confirmar/cancelar: **sem body** (ou body vazio).

### 3.5 Contratos de DTO (princípios)

- Request **não** expõe: `id` (create), `vagasOcupadas`, timestamps de auditoria.
- `vagasOcupadas` e mudanças de status de matrícula: **somente service**.
- Cliente **não** envia `status` na criação de matrícula (sempre `PENDENTE`).
- Response de matrícula pode incluir `alunoNome`, `turmaCodigo` para facilitar a UI.

#### Exemplos

**AlunoCreateRequest / AlunoUpdateRequest**

```json
{
  "nome": "Maria Silva",
  "email": "maria@email.com",
  "cpf": "12345678901",
  "matriculaAcademica": "RA2026001",
  "dataNascimento": "2000-05-10",
  "ativo": true
}
```

**TurmaCreateRequest**

```json
{
  "codigo": "BD-2026-1A",
  "disciplinaId": "uuid",
  "ano": 2026,
  "periodo": "1",
  "limiteVagas": 40,
  "status": "ABERTA"
}
```

**MatriculaCreateRequest**

```json
{
  "alunoId": "uuid",
  "turmaId": "uuid"
}
```

### 3.6 Validação e erros

#### Bean Validation (DTOs)

- `@NotBlank`, `@NotNull`, `@Email`, `@Size`, `@Positive`, `@Min`, `@Pattern` (CPF 11 dígitos)

#### Exceções de domínio

| Exceção | HTTP |
|---------|------|
| Validação de entrada | 400 |
| `ResourceNotFoundException` | 404 |
| `BusinessRuleException` / conflito (duplicata, sem vaga, transição inválida, exclusão com vínculo) | 409 |
| Erro inesperado | 500 |

#### ErrorResponse padronizado

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

Para `400`, `details` com lista `{ "field": "email", "message": "..." }`.

Centralizar em `@RestControllerAdvice`.

### 3.7 Services

Um service por recurso: `AlunoService`, `CursoService`, `DisciplinaService`, `TurmaService`, `MatriculaService`.

- `@Transactional` em criar/confirmar/cancelar matrícula.
- Confirmação e ajuste de `vagasOcupadas` na mesma transação.
- Optimistic lock via `@Version` em `Turma`.

---

## 4. Tópico 3 — Arquitetura do frontend

### 4.1 Decisões confirmadas

| # | Decisão |
|---|--------|
| Organização | Angular **standalone** (sem NgModules clássicos) |
| UI | **Angular Material** |
| Layout | Menu lateral fixo + área de conteúdo |
| CPF | Máscara no input; envio à API **somente dígitos** |
| Após criar matrícula | Permanecer na tela e oferecer **“Confirmar agora”** |
| Select de turmas (matrícula) | Listar somente turmas **ABERTA** |

### 4.2 Stack

| Item | Escolha |
|------|---------|
| Angular | LTS atual (17+), standalone |
| Linguagem | TypeScript |
| Forms | Reactive Forms |
| HTTP | `HttpClient` |
| Estado | local nos components + services (sem NgRx no MVP) |

### 4.3 Estrutura de pastas

```text
src/app/
├── core/
│   ├── interceptors/
│   ├── models/
│   ├── services/
│   └── constants/
├── shared/
│   ├── components/
│   └── pipes/
└── features/
    ├── alunos/
    ├── cursos/
    ├── disciplinas/
    ├── turmas/
    └── matriculas/
```

Cada feature: list + form + `*.service.ts` + rotas.

### 4.4 Telas mínimas

- Alunos, Cursos, Disciplinas, Turmas: listagem + formulário (criar/editar)
- Matrículas:
  - Nova matrícula
  - Consulta por aluno
  - Consulta por turma
  - Ações Confirmar / Cancelar conforme status

Sub-rotas/abas de matrículas: Nova · Por aluno · Por turma.

### 4.5 Models TypeScript

- IDs sempre `string` (UUID).
- Datas: string ISO da API; formatar na exibição para **horário de Brasília** / `pt-BR`.
- Enums tipados: `StatusTurma`, `StatusMatricula`.
- Separar `*Request` e entidades de response.
- `vagasOcupadas`: somente leitura na UI.

### 4.6 Services HTTP

Um service por recurso apontando para a API via proxy (`/api/v1`).

`MatriculaService`: `criar`, `buscarPorId`, `confirmar`, `cancelar`, `listarPorAluno`, `listarPorTurma`.

Sem lógica de negócio pesada no front — regras ficam no backend.

### 4.7 Validação de formulários (UX)

| Formulário | Validações |
|------------|------------|
| Aluno | nome, email, cpf (11 dígitos), matriculaAcademica |
| Curso | nome, codigo |
| Disciplina | nome, codigo, cargaHoraria > 0, cursoId |
| Turma | codigo, disciplinaId, ano, periodo (`1`/`2`), limiteVagas ≥ 1, status |
| Matrícula | alunoId, turmaId |

### 4.8 Interceptor de erros

| Status | UI |
|--------|-----|
| 400 | erros por campo (`details`) no formulário |
| 404 | “Recurso não encontrado” |
| 409 | mensagem de negócio do backend |
| 500 / rede | “Erro inesperado. Tente novamente.” |

### 4.9 Regras de UI na matrícula

- Confirmar visível só se `PENDENTE`
- Cancelar visível se `PENDENTE` ou `CONFIRMADA`
- `CANCELADA`: somente leitura
- Exclusão: modal de confirmação; 409 exibe `message` do backend

---

## 5. Tópico 4 — Banco, Docker e ambiente

### 5.1 Decisões confirmadas

| # | Decisão |
|---|--------|
| Banco | **PostgreSQL 16**, database/user/password `matricula` |
| DDL no MVP | `spring.jpa.hibernate.ddl-auto=update` |
| Compose | 3 serviços: `db`, `backend`, `frontend` |
| Proxy | `/api` no nginx do frontend + `proxy.conf.json` no `ng serve` |
| Porta frontend (host) | **4200** |
| Encoding / fuso | **UTF-8** + **UTC** no backend/banco + **ISO** na API + exibição em **America/Sao_Paulo** / `pt-BR` no Angular |

### 5.2 Portas

| Serviço | Porta host | Porta container |
|---------|------------|-----------------|
| PostgreSQL | 5432 | 5432 |
| Backend | 8080 | 8080 |
| Frontend | 4200 | 80 (nginx) |

### 5.3 Variáveis (referência)

**db**

```env
POSTGRES_DB=matricula
POSTGRES_USER=matricula
POSTGRES_PASSWORD=matricula
```

**backend**

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/matricula
SPRING_DATASOURCE_USERNAME=matricula
SPRING_DATASOURCE_PASSWORD=matricula
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SERVER_PORT=8080
```

**frontend (browser)**

- Em produção Docker: proxy nginx `/api` → `backend:8080`
- Em dev: `proxy.conf.json` → `http://localhost:8080`
- O browser **não** deve chamar o hostname Docker `backend` diretamente

### 5.4 Estrutura do repositório

```text
gestao-matricula/
├── backend/                 # Spring Boot (Maven), pacote br.edu.com.matricula
├── frontend/                # Angular standalone
├── docs/                    # este planejamento
├── docker-compose.yml
├── .env.example
└── README.md
```

### 5.5 Modos de execução

**A) Demo / entrega**

```bash
docker compose up --build
```

**B) Desenvolvimento**

```bash
docker compose up db
# backend: ./mvnw spring-boot:run
# frontend: ng serve (com proxy.conf.json)
```

### 5.6 Datas e fuso (obrigatório)

- Armazenar timestamps em **UTC**
- API em **ISO 8601** (ex.: `...Z` ou com offset)
- `dataNascimento` como data (`LocalDate` / `date`) sem hora
- Angular formata instantes para o usuário em horário de Brasília
- História para o README: *“armazenamos em UTC, apresentamos no fuso do usuário”*

### 5.7 Volumes

- Volume `pgdata` para persistir Postgres
- Documentar no README que `docker compose down -v` apaga dados

---

## 6. Tópico 5 — Fases de implementação e entrega

### 6.1 Decisões confirmadas

| # | Decisão |
|---|--------|
| Ordem | Fases **0 → 1 → 2 → 3** |
| Repo | **Monorepo** |
| Backlog se sobrar tempo | Testes do `MatriculaService` |
| Nome | `gestao-matricula` |
| Pacote Java | `br.edu.com.matricula` |

### 6.2 Fase 0 — Esqueleto do repositório

**Status: CONCLUÍDA** — detalhes em [`FASE-0-EXECUCAO.md`](./FASE-0-EXECUCAO.md)

1. Criar `backend/`, `frontend/`, `docker-compose.yml`, `.env.example`, README inicial
2. Subir só Postgres via Compose e validar conexão
3. **Não** implementar domínio ainda nesta fase além do necessário para o esqueleto

### 6.3 Fase 1 — Backend

1. Spring Boot + Maven + dependências (Web, JPA, Validation, PostgreSQL, Springdoc)
2. Pacotes, `ErrorResponse`, `GlobalExceptionHandler`, CORS/config, OpenAPI
3. Enums + entidades + repositórios
4. DTOs + mappers + services/controllers dos CRUDs na ordem: Aluno → Curso → Disciplina → Turma
5. `MatriculaService` + endpoints (criar / confirmar / cancelar / consultas) + `@Version` na turma
6. Smoke manual via Swagger ou curl

### 6.4 Fase 2 — Frontend

1. Angular standalone + Material + layout (menu lateral)
2. Environment + `proxy.conf.json` + interceptor de erros
3. Models + services HTTP
4. Telas CRUD na ordem: alunos → cursos → disciplinas → turmas
5. Telas de matrícula (nova + por aluno + por turma + “Confirmar agora”)

### 6.5 Fase 3 — Docker e entrega

1. Dockerfile backend (multi-stage Maven → JRE)
2. Dockerfile frontend (multi-stage Node build → nginx com proxy `/api` e SPA fallback)
3. `docker compose up --build` end-to-end
4. README final
5. Validar checklist de demo

### 6.6 Dependências entre fases

```text
Fase 0 → Fase 1 (API completa) → Fase 2 (UI) → Fase 3 (Compose completo + README)
         └─ Matrícula só depois de Aluno e Turma existirem
```

---

## 7. Checklist de demo

Usar para validar a entrega:

1. Cadastrar curso → disciplina → turma (`ABERTA`, limite de vagas)
2. Cadastrar aluno (CPF + RA)
3. Criar matrícula → `PENDENTE` → `vagasOcupadas` **não** muda
4. Confirmar → `CONFIRMADA` → `vagasOcupadas` +1
5. Segunda matrícula mesma turma → **409**
6. Cancelar confirmada → vaga liberada
7. Fechar turma → nova matrícula/confirmação bloqueadas
8. Consultar por aluno e por turma
9. Excluir curso com disciplina → **409**

---

## 8. README de entrega — estrutura obrigatória

O README final deve conter:

```markdown
# Gestão de Matrículas Acadêmicas

## Visão geral
## Stack
## Pré-requisitos
## Como executar
  ### Docker Compose (recomendado)
  ### Desenvolvimento local (db no Docker + apps locais)
## Acessos
  - Frontend: http://localhost:4200
  - API: http://localhost:8080/api/v1
  - Swagger: (path Springdoc)
## Principais decisões técnicas
## Modelo de domínio e regras de negócio
## Endpoints principais
## Tratamento de erros
## Limitações do MVP
## Backlog
```

### Decisões técnicas a destacar no README

- Camadas controller / service / domain / repository / DTO
- UUID; aluno com CPF + matrícula acadêmica (RA)
- Matrícula `PENDENTE` → confirmar/cancelar; vaga só na confirmação
- Unique `(aluno, turma)`; exclusão bloqueada com vínculo
- Optimistic lock (`@Version`) na turma
- Erros padronizados; negócio em 409
- UTC no backend + ISO na API + exibição em Brasília no Angular
- Proxy `/api` (nginx / `proxy.conf.json`)
- `ddl-auto: update` no MVP; migrations no backlog

### Critério de pronto para entregar

- [ ] Compose sobe os 3 serviços sem passo manual obscuro
- [ ] README permite outra pessoa rodar só com o que está escrito
- [ ] Fluxo de matrícula (checklist) funciona pela UI
- [ ] Swagger reflete a API
- [ ] Backlog e limitações explícitos

---

## 9. Instruções para agentes

1. **Ler este arquivo por completo** e o relatório da fase em andamento antes de gerar código.
2. Respeitar MVP vs backlog: não implementar testes, migrations, mensageria ou docs extras sem pedido.
3. Seguir a ordem das fases; dentro do backend, CRUDs na ordem Aluno → Curso → Disciplina → Turma → Matrícula.
4. Pacote Java raiz: `br.edu.com.matricula`.
5. Nome do projeto: `gestao-matricula`.
6. Qualquer divergência em relação a este documento deve ser **perguntada ao usuário** antes de implementar.
7. Ao concluir uma fase: atualizar a seção [Andamento das fases](#10-andamento-das-fases), criar `docs/FASE-N-EXECUCAO.md` e referenciá-lo aqui; **não** iniciar a fase seguinte até validação humana.
8. **Próxima fase após validação da Fase 0:** **Fase 1 — Backend**.

---

## 10. Andamento das fases

| Fase | Descrição | Status | Relatório |
|------|-----------|--------|-----------|
| 0 | Esqueleto do repositório + Postgres | **Concluída** (aguardando validação) | [`FASE-0-EXECUCAO.md`](./FASE-0-EXECUCAO.md) |
| 1 | Backend (API REST) | Pendente | — |
| 2 | Frontend (Angular) | Pendente | — |
| 3 | Docker completo + README final | Pendente | — |

---

## Histórico de alinhamento

Planejamento elaborado por tópicos com confirmação do usuário:

| Tópico | Conteúdo | Status |
|--------|----------|--------|
| 1 | Entidades, enums, regras, transições de matrícula | Fechado |
| 2 | Backend (Maven, pacotes, DTOs, erros, OpenAPI, `@Version`) | Fechado |
| 3 | Frontend (standalone, Material, telas, interceptor, UX matrícula) | Fechado |
| 4 | PostgreSQL, Compose, proxy, portas, UTC/ISO/Brasília | Fechado |
| 5 | Fases 0–3, monorepo, nome, pacote, README, backlog | Fechado |

| Marco | Status |
|-------|--------|
| Validação de `DECISOES-TECNICAS.md` | Feita pelo usuário |
| Execução da Fase 0 | Concluída — ver [`FASE-0-EXECUCAO.md`](./FASE-0-EXECUCAO.md) |
| Validação do relatório da Fase 0 | **Pendente** |
| Próximo passo após validação | Iniciar **Fase 1 — Backend** |
)
