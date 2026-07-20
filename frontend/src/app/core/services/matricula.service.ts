import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../constants/api.constants';
import { Matricula, MatriculaCreateRequest } from '../models/matricula';

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

  listarPorAluno(alunoId: string): Observable<Matricula[]> {
    return this.http.get<Matricula[]>(`${this.baseUrl}/aluno/${alunoId}`);
  }

  listarPorTurma(turmaId: string): Observable<Matricula[]> {
    return this.http.get<Matricula[]>(`${this.baseUrl}/turma/${turmaId}`);
  }
}
