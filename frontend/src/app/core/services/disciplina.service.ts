import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../constants/api.constants';
import { Disciplina, DisciplinaRequest } from '../models/disciplina';

@Injectable({ providedIn: 'root' })
export class DisciplinaService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/disciplinas`;

  listar(): Observable<Disciplina[]> {
    return this.http.get<Disciplina[]>(this.baseUrl);
  }

  buscarPorId(id: string): Observable<Disciplina> {
    return this.http.get<Disciplina>(`${this.baseUrl}/${id}`);
  }

  criar(request: DisciplinaRequest): Observable<Disciplina> {
    return this.http.post<Disciplina>(this.baseUrl, request);
  }

  atualizar(id: string, request: DisciplinaRequest): Observable<Disciplina> {
    return this.http.put<Disciplina>(`${this.baseUrl}/${id}`, request);
  }

  excluir(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
