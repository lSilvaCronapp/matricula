export interface Curso {
  id: string;
  nome: string;
  codigo: string;
  descricao: string | null;
  ativo: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CursoRequest {
  nome: string;
  codigo: string;
  descricao: string | null;
  ativo: boolean;
}
