import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../constants/api.constants';
import { StatusTurma } from '../models/enums';
import { PageRequest, PageResponse } from '../models/page';
import { Turma, TurmaRequest } from '../models/turma';
import { toHttpParams } from '../utils/http-params.util';

export interface TurmaPageRequest extends PageRequest {
  status?: StatusTurma;
}

@Injectable({ providedIn: 'root' })
export class TurmaService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/turmas`;

  listar(request: TurmaPageRequest = {}): Observable<PageResponse<Turma>> {
    return this.http.get<PageResponse<Turma>>(this.baseUrl, {
      params: toHttpParams({ page: 0, size: 10, ...request })
    });
  }

  buscarPorId(id: string): Observable<Turma> {
    return this.http.get<Turma>(`${this.baseUrl}/${id}`);
  }

  criar(request: TurmaRequest): Observable<Turma> {
    return this.http.post<Turma>(this.baseUrl, request);
  }

  atualizar(id: string, request: TurmaRequest): Observable<Turma> {
    return this.http.put<Turma>(`${this.baseUrl}/${id}`, request);
  }

  excluir(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
