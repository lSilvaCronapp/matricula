import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../constants/api.constants';
import { Curso, CursoRequest } from '../models/curso';
import { PageRequest, PageResponse } from '../models/page';
import { toHttpParams } from '../utils/http-params.util';

@Injectable({ providedIn: 'root' })
export class CursoService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/cursos`;

  listar(request: PageRequest = {}): Observable<PageResponse<Curso>> {
    return this.http.get<PageResponse<Curso>>(this.baseUrl, {
      params: toHttpParams({ page: 0, size: 10, ...request })
    });
  }

  buscarPorId(id: string): Observable<Curso> {
    return this.http.get<Curso>(`${this.baseUrl}/${id}`);
  }

  criar(request: CursoRequest): Observable<Curso> {
    return this.http.post<Curso>(this.baseUrl, request);
  }

  atualizar(id: string, request: CursoRequest): Observable<Curso> {
    return this.http.put<Curso>(`${this.baseUrl}/${id}`, request);
  }

  excluir(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
