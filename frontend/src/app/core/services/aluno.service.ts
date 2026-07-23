import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../constants/api.constants';
import { Aluno, AlunoRequest } from '../models/aluno';
import { PageRequest, PageResponse } from '../models/page';
import { toHttpParams } from '../utils/http-params.util';

@Injectable({ providedIn: 'root' })
export class AlunoService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/alunos`;

  listar(request: PageRequest = {}): Observable<PageResponse<Aluno>> {
    return this.http.get<PageResponse<Aluno>>(this.baseUrl, {
      params: toHttpParams({ page: 0, size: 10, ...request })
    });
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
