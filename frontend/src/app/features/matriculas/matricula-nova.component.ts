import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { Aluno } from '../../core/models/aluno';
import { Matricula } from '../../core/models/matricula';
import { Turma } from '../../core/models/turma';
import { AlunoService } from '../../core/services/aluno.service';
import { MatriculaService } from '../../core/services/matricula.service';
import { TurmaService } from '../../core/services/turma.service';
import { BrasiliaDatePipe } from '../../shared/pipes/brasilia-date.pipe';
import { applyServerFieldErrors } from '../../shared/utils/form-error.util';

@Component({
  selector: 'app-matricula-nova',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSelectModule,
    MatSnackBarModule,
    MatCardModule,
    MatChipsModule,
    BrasiliaDatePipe
  ],
  templateUrl: './matricula-nova.component.html',
  styleUrl: './matricula-nova.component.scss'
})
export class MatriculaNovaComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly alunoService = inject(AlunoService);
  private readonly turmaService = inject(TurmaService);
  private readonly matriculaService = inject(MatriculaService);
  private readonly snackBar = inject(MatSnackBar);

  readonly form = this.fb.nonNullable.group({
    alunoId: ['', Validators.required],
    turmaId: ['', Validators.required]
  });

  alunos: Aluno[] = [];
  turmasAbertas: Turma[] = [];
  saving = false;
  confirming = false;
  ultimaMatricula: Matricula | null = null;

  ngOnInit(): void {
    this.alunoService.listar().subscribe({
      next: (alunos) => {
        this.alunos = alunos.filter((a) => a.ativo);
      }
    });
    this.turmaService.listar('ABERTA').subscribe({
      next: (turmas) => {
        this.turmasAbertas = turmas;
      }
    });
  }

  criar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;
    this.ultimaMatricula = null;
    this.matriculaService.criar(this.form.getRawValue()).subscribe({
      next: (matricula) => {
        this.ultimaMatricula = matricula;
        this.saving = false;
        this.snackBar.open('Matrícula criada como PENDENTE.', 'Fechar', { duration: 4000 });
      },
      error: (err) => {
        applyServerFieldErrors(this.form, err);
        this.saving = false;
      }
    });
  }

  confirmarAgora(): void {
    if (!this.ultimaMatricula || this.ultimaMatricula.status !== 'PENDENTE') {
      return;
    }

    this.confirming = true;
    this.matriculaService.confirmar(this.ultimaMatricula.id).subscribe({
      next: (matricula) => {
        this.ultimaMatricula = matricula;
        this.confirming = false;
        this.snackBar.open('Matrícula confirmada.', 'Fechar', { duration: 4000 });
      },
      error: () => {
        this.confirming = false;
      }
    });
  }

  fieldError(controlName: string): string | null {
    const control = this.form.get(controlName);
    if (!control || !control.touched || !control.errors) {
      return null;
    }
    if (control.errors['server']) {
      return control.errors['server'];
    }
    if (control.errors['required']) {
      return 'Campo obrigatório';
    }
    return 'Valor inválido';
  }
}
