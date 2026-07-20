import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../constants/api.constants';
import { Aluno, AlunoRequest } from '../models/aluno';

@Injectable({ providedIn: 'root' })
export class AlunoService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/alunos`;

  listar(): Observable<Aluno[]> {
    return this.http.get<Aluno[]>(this.baseUrl);
  }

  buscarPorId(id: string): Observable<Aluno> {
    return this.http.get<Aluno>(`${this.baseUrl}/${id}`);
  }

  criar(request: AlunoRequest): Observable<Aluno> {
    return this.http.post<Aluno>(this.baseUrl, request);
  }

  atualizar(id: string, request: AlunoRequest): Observable<Aluno> {
    return this.http.put<Aluno>(`${this.baseUrl}/${id}`, request);
  }

  excluir(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
