import { AsyncPipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { Observable, debounceTime, distinctUntilChanged, filter, map, switchMap } from 'rxjs';
import { Matricula } from '../../core/models/matricula';
import { Turma } from '../../core/models/turma';
import { MatriculaService } from '../../core/services/matricula.service';
import { TurmaService } from '../../core/services/turma.service';
import { autocompleteSearch } from '../../core/utils/autocomplete-search.util';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog.component';
import { BrasiliaDatePipe } from '../../shared/pipes/brasilia-date.pipe';

@Component({
  selector: 'app-matricula-por-turma',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    AsyncPipe,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatAutocompleteModule,
    MatTableModule,
    MatSnackBarModule,
    MatChipsModule,
    MatDialogModule,
    MatPaginatorModule,
    BrasiliaDatePipe
  ],
  templateUrl: './matricula-por-turma.component.html',
  styleUrl: './matricula-por-turma.component.scss'
})
export class MatriculaPorTurmaComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly turmaService = inject(TurmaService);
  private readonly matriculaService = inject(MatriculaService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly dialog = inject(MatDialog);

  readonly form = this.fb.nonNullable.group({
    turmaId: ['', Validators.required]
  });
  readonly turmaSearch = new FormControl<string | Turma>('', { nonNullable: true });
  readonly resultSearch = new FormControl('', { nonNullable: true });

  turmasFiltradas$!: Observable<Turma[]>;
  matriculas: Matricula[] = [];
  loading = false;
  consulted = false;
  totalElements = 0;
  pageIndex = 0;
  pageSize = 10;
  displayedColumns = [
    'alunoNome',
    'status',
    'dataSolicitacao',
    'dataConfirmacao',
    'dataCancelamento',
    'acoes'
  ];

  ngOnInit(): void {
    this.turmasFiltradas$ = autocompleteSearch(
      this.turmaSearch.valueChanges,
      this.turmaSearch.value,
      (term) => (typeof term === 'string' ? term.trim() : ''),
      (q) =>
        this.turmaService
          .listar({ page: 0, size: 10, q })
          .pipe(map((page) => page.content ?? [])),
      () => this.form.controls.turmaId.setValue('')
    );

    this.resultSearch.valueChanges.pipe(debounceTime(300), distinctUntilChanged()).subscribe(() => {
      if (this.consulted) {
        this.pageIndex = 0;
        this.consultar(false);
      }
    });
  }

  displayTurma = (value: string | Turma): string => {
    if (!value) {
      return '';
    }
    if (typeof value === 'string') {
      return value;
    }
    return `${value.codigo} — ${value.disciplinaNome}`;
  };

  selecionarTurma(turma: Turma): void {
    this.form.controls.turmaId.setValue(turma.id);
  }

  consultar(resetPage = true): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    if (resetPage) {
      this.pageIndex = 0;
    }

    this.loading = true;
    this.consulted = true;
    this.matriculaService
      .listarPorTurma(this.form.controls.turmaId.value, {
        page: this.pageIndex,
        size: this.pageSize,
        q: this.resultSearch.value.trim() || undefined
      })
      .subscribe({
        next: (page) => {
          this.matriculas = page.content ?? [];
          this.totalElements = page.totalElements ?? 0;
          this.loading = false;
        },
        error: () => {
          this.matriculas = [];
          this.loading = false;
        }
      });
  }

  onPage(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.consultar(false);
  }

  confirmar(matricula: Matricula): void {
    this.matriculaService.confirmar(matricula.id).subscribe({
      next: () => {
        this.snackBar.open('Matrícula confirmada.', 'Fechar', { duration: 3000 });
        this.consultar(false);
      }
    });
  }

  cancelar(matricula: Matricula): void {
    this.dialog
      .open(ConfirmDialogComponent, {
        data: {
          title: 'Cancelar matrícula',
          message: `Cancelar a matrícula de ${matricula.alunoNome} em ${matricula.turmaCodigo}?`,
          confirmLabel: 'Cancelar matrícula'
        }
      })
      .afterClosed()
      .pipe(
        filter(Boolean),
        switchMap(() => this.matriculaService.cancelar(matricula.id))
      )
      .subscribe({
        next: () => {
          this.snackBar.open('Matrícula cancelada.', 'Fechar', { duration: 3000 });
          this.consultar(false);
        }
      });
  }
}
