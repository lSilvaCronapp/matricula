import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import {
  Subject,
  Subscription,
  debounceTime,
  distinctUntilChanged,
  filter,
  merge,
  of,
  switchMap,
  tap
} from 'rxjs';
import { Disciplina } from '../../core/models/disciplina';
import { DisciplinaService } from '../../core/services/disciplina.service';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog.component';
import { BrasiliaDatePipe } from '../../shared/pipes/brasilia-date.pipe';

@Component({
  selector: 'app-disciplina-list',
  standalone: true,
  imports: [
    RouterLink,
    ReactiveFormsModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatSnackBarModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    MatFormFieldModule,
    MatInputModule,
    MatPaginatorModule,
    BrasiliaDatePipe
  ],
  templateUrl: './disciplina-list.component.html',
  styleUrl: './disciplina-list.component.scss'
})
export class DisciplinaListComponent implements OnInit, OnDestroy {
  private readonly disciplinaService = inject(DisciplinaService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);
  private readonly reload$ = new Subject<void>();
  private loadSub?: Subscription;

  readonly searchControl = new FormControl('', { nonNullable: true });
  disciplinas: Disciplina[] = [];
  loading = true;
  totalElements = 0;
  pageIndex = 0;
  pageSize = 10;
  displayedColumns = ['codigo', 'nome', 'cursoNome', 'cargaHoraria', 'ativo', 'updatedAt', 'acoes'];

  ngOnInit(): void {
    this.loadSub = merge(
      of(null),
      this.searchControl.valueChanges.pipe(
        debounceTime(300),
        distinctUntilChanged(),
        tap(() => {
          this.pageIndex = 0;
        })
      ),
      this.reload$
    )
      .pipe(
        switchMap(() => {
          this.loading = true;
          return this.disciplinaService.listar({
            page: this.pageIndex,
            size: this.pageSize,
            q: this.searchControl.value.trim() || undefined
          });
        })
      )
      .subscribe({
        next: (page) => {
          this.disciplinas = page.content ?? [];
          this.totalElements = page.totalElements ?? 0;
          this.loading = false;
        },
        error: () => {
          this.disciplinas = [];
          this.loading = false;
        }
      });
  }

  ngOnDestroy(): void {
    this.loadSub?.unsubscribe();
  }

  carregar(): void {
    this.reload$.next();
  }

  onPage(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.carregar();
  }

  excluir(disciplina: Disciplina): void {
    this.dialog
      .open(ConfirmDialogComponent, {
        data: {
          title: 'Excluir disciplina',
          message: `Deseja excluir a disciplina "${disciplina.nome}"?`
        }
      })
      .afterClosed()
      .pipe(
        filter(Boolean),
        switchMap(() => this.disciplinaService.excluir(disciplina.id))
      )
      .subscribe({
        next: () => {
          this.snackBar.open('Disciplina excluída.', 'Fechar', { duration: 3000 });
          this.carregar();
        }
      });
  }
}
