import { StatusTurma } from './enums';

export interface Turma {
  id: string;
  codigo: string;
  disciplinaId: string;
  disciplinaNome: string;
  ano: number;
  periodo: string;
  limiteVagas: number;
  vagasOcupadas: number;
  status: StatusTurma;
  version: number;
  createdAt: string;
  updatedAt: string;
}

export interface TurmaRequest {
  codigo: string;
  disciplinaId: string;
  ano: number;
  periodo: string;
  limiteVagas: number;
  status: StatusTurma;
}
