import { StatusMatricula } from './enums';

export interface Matricula {
  id: string;
  alunoId: string;
  alunoNome: string;
  turmaId: string;
  turmaCodigo: string;
  disciplinaId: string;
  disciplinaNome: string;
  status: StatusMatricula;
  dataSolicitacao: string;
  dataConfirmacao: string | null;
  dataCancelamento: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface MatriculaCreateRequest {
  alunoId: string;
  turmaId: string;
}
