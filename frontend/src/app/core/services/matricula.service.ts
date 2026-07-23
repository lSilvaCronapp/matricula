import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../constants/api.constants';
import { Matricula, MatriculaCreateRequest } from '../models/matricula';
import { PageRequest, PageResponse } from '../models/page';
import { toHttpParams } from '../utils/http-params.util';

@Injectable({ providedIn: 'root' })
export class MatriculaService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/matriculas`;

  criar(request: MatriculaCreateRequest): Observable<Matricula> {
    return this.http.post<Matricula>(this.baseUrl, request);
  }

  buscarPorId(id: string): Observable<Matricula> {
    return this.http.get<Matricula>(`${this.baseUrl}/${id}`);
  }

  confirmar(id: string): Observable<Matricula> {
    return this.http.patch<Matricula>(`${this.baseUrl}/${id}/confirmar`, null);
  }

  cancelar(id: string): Observable<Matricula> {
    return this.http.patch<Matricula>(`${this.baseUrl}/${id}/cancelar`, null);
  }

  listarPorAluno(alunoId: string, request: PageRequest = {}): Observable<PageResponse<Matricula>> {
    return this.http.get<PageResponse<Matricula>>(`${this.baseUrl}/aluno/${alunoId}`, {
      params: toHttpParams({ page: 0, size: 10, ...request })
    });
  }

  listarPorTurma(turmaId: string, request: PageRequest = {}): Observable<PageResponse<Matricula>> {
    return this.http.get<PageResponse<Matricula>>(`${this.baseUrl}/turma/${turmaId}`, {
      params: toHttpParams({ page: 0, size: 10, ...request })
    });
  }
}
