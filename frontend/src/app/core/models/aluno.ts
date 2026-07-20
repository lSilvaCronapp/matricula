export interface Aluno {
  id: string;
  nome: string;
  email: string;
  cpf: string;
  matriculaAcademica: string;
  dataNascimento: string | null;
  ativo: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface AlunoRequest {
  nome: string;
  email: string;
  cpf: string;
  matriculaAcademica: string;
  dataNascimento: string | null;
  ativo: boolean;
}
