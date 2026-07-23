import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { AsyncPipe } from '@angular/common';
import { Observable, map } from 'rxjs';
import { Aluno } from '../../core/models/aluno';
import { Matricula } from '../../core/models/matricula';
import { Turma } from '../../core/models/turma';
import { AlunoService } from '../../core/services/aluno.service';
import { MatriculaService } from '../../core/services/matricula.service';
import { TurmaService } from '../../core/services/turma.service';
import { autocompleteSearch } from '../../core/utils/autocomplete-search.util';
import { BrasiliaDatePipe } from '../../shared/pipes/brasilia-date.pipe';
import { applyServerFieldErrors } from '../../shared/utils/form-error.util';

@Component({
  selector: 'app-matricula-nova',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    AsyncPipe,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatAutocompleteModule,
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

  readonly alunoSearch = new FormControl<string | Aluno>('', { nonNullable: true });
  readonly turmaSearch = new FormControl<string | Turma>('', { nonNullable: true });

  alunosFiltrados$!: Observable<Aluno[]>;
  turmasFiltradas$!: Observable<Turma[]>;

  saving = false;
  confirming = false;
  ultimaMatricula: Matricula | null = null;

  ngOnInit(): void {
    this.alunosFiltrados$ = autocompleteSearch(
      this.alunoSearch.valueChanges,
      this.alunoSearch.value,
      (term) => (typeof term === 'string' ? term.trim() : ''),
      (q) =>
        this.alunoService.listar({ page: 0, size: 10, q }).pipe(
          map((page) => (page.content ?? []).filter((a) => a.ativo))
        ),
      () => this.form.controls.alunoId.setValue('')
    );

    this.turmasFiltradas$ = autocompleteSearch(
      this.turmaSearch.valueChanges,
      this.turmaSearch.value,
      (term) => (typeof term === 'string' ? term.trim() : ''),
      (q) =>
        this.turmaService
          .listar({ page: 0, size: 10, status: 'ABERTA', q })
          .pipe(map((page) => page.content ?? [])),
      () => this.form.controls.turmaId.setValue('')
    );
  }

  displayAluno = (value: string | Aluno): string => {
    if (!value) {
      return '';
    }
    if (typeof value === 'string') {
      return value;
    }
    return `${value.matriculaAcademica} — ${value.nome}`;
  };

  displayTurma = (value: string | Turma): string => {
    if (!value) {
      return '';
    }
    if (typeof value === 'string') {
      return value;
    }
    return `${value.codigo} — ${value.disciplinaNome}`;
  };

  selecionarAluno(aluno: Aluno): void {
    this.form.controls.alunoId.setValue(aluno.id);
  }

  selecionarTurma(turma: Turma): void {
    this.form.controls.turmaId.setValue(turma.id);
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
