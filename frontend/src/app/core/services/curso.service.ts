import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../constants/api.constants';
import { Curso, CursoRequest } from '../models/curso';

@Injectable({ providedIn: 'root' })
export class CursoService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/cursos`;

  listar(): Observable<Curso[]> {
    return this.http.get<Curso[]>(this.baseUrl);
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
