import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
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
import { Turma } from '../../core/models/turma';
import { TurmaService } from '../../core/services/turma.service';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog.component';
import { BrasiliaDatePipe } from '../../shared/pipes/brasilia-date.pipe';

@Component({
  selector: 'app-turma-list',
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
    MatChipsModule,
    MatFormFieldModule,
    MatInputModule,
    MatPaginatorModule,
    BrasiliaDatePipe
  ],
  templateUrl: './turma-list.component.html',
  styleUrl: './turma-list.component.scss'
})
export class TurmaListComponent implements OnInit, OnDestroy {
  private readonly turmaService = inject(TurmaService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);
  private readonly reload$ = new Subject<void>();
  private loadSub?: Subscription;

  readonly searchControl = new FormControl('', { nonNullable: true });
  turmas: Turma[] = [];
  loading = true;
  totalElements = 0;
  pageIndex = 0;
  pageSize = 10;
  displayedColumns = [
    'codigo',
    'disciplinaNome',
    'ano',
    'periodo',
    'vagas',
    'status',
    'updatedAt',
    'acoes'
  ];

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
          return this.turmaService.listar({
            page: this.pageIndex,
            size: this.pageSize,
            q: this.searchControl.value.trim() || undefined
          });
        })
      )
      .subscribe({
        next: (page) => {
          this.turmas = page.content ?? [];
          this.totalElements = page.totalElements ?? 0;
          this.loading = false;
        },
        error: () => {
          this.turmas = [];
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

  excluir(turma: Turma): void {
    this.dialog
      .open(ConfirmDialogComponent, {
        data: {
          title: 'Excluir turma',
          message: `Deseja excluir a turma "${turma.codigo}"?`
        }
      })
      .afterClosed()
      .pipe(
        filter(Boolean),
        switchMap(() => this.turmaService.excluir(turma.id))
      )
      .subscribe({
        next: () => {
          this.snackBar.open('Turma excluída.', 'Fechar', { duration: 3000 });
          this.carregar();
        }
      });
  }
}
