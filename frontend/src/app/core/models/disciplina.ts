export interface Disciplina {
  id: string;
  nome: string;
  codigo: string;
  cargaHoraria: number;
  cursoId: string;
  cursoNome: string;
  ativo: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface DisciplinaRequest {
  nome: string;
  codigo: string;
  cargaHoraria: number;
  cursoId: string;
  ativo: boolean;
}
