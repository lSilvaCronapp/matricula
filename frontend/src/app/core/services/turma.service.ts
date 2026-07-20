import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../constants/api.constants';
import { StatusTurma } from '../models/enums';
import { Turma, TurmaRequest } from '../models/turma';

@Injectable({ providedIn: 'root' })
export class TurmaService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/turmas`;

  listar(status?: StatusTurma): Observable<Turma[]> {
    let params = new HttpParams();
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<Turma[]>(this.baseUrl, { params });
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
