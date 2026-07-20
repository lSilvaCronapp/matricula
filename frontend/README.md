# Frontend — Gestão de Matrículas Acadêmicas

Angular **19** (standalone) + Angular Material, conforme `docs/DECISOES-TECNICAS.md`.

## Stack

- Angular standalone + TypeScript + Reactive Forms
- Angular Material (tema azure-blue)
- Proxy `/api` → `http://localhost:8080` (`proxy.conf.json`)

## Pré-requisitos

- Node.js 20+ (ou Docker com imagem `node:22`)
- Backend rodando em `http://localhost:8080` (Fase 1)
- Postgres via `docker compose up -d db`

## Como executar (desenvolvimento)

```bash
cd frontend
npm install
npm start
```

Acesse: http://localhost:4200

### Via Docker (sem Node local)

```bash
docker run --rm -it \
  -v "$PWD/frontend":/workspace \
  -w /workspace \
  -p 4200:4200 \
  --network host \
  node:22-bookworm \
  bash -c "npm install && npx ng serve --host 0.0.0.0 --proxy-config proxy.conf.json"
```

## Estrutura

```text
src/app/
├── core/          # models, services HTTP, interceptor, constants
├── shared/        # pipes, diretivas, diálogo de confirmação
├── layout/        # menu lateral + área de conteúdo
└── features/      # alunos, cursos, disciplinas, turmas, matriculas
```

## Features

| Rota | Descrição |
|------|-----------|
| `/alunos` | CRUD de alunos (CPF mascarado; envio só dígitos) |
| `/cursos` | CRUD de cursos |
| `/disciplinas` | CRUD de disciplinas |
| `/turmas` | CRUD de turmas (`vagasOcupadas` somente leitura) |
| `/matriculas/nova` | Nova matrícula + “Confirmar agora” |
| `/matriculas/por-aluno` | Consulta e ações por aluno |
| `/matriculas/por-turma` | Consulta e ações por turma |

Datas da API (UTC/ISO) são exibidas em `America/Sao_Paulo` / `pt-BR`.
