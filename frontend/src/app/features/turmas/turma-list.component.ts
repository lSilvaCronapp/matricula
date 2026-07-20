import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { filter, switchMap } from 'rxjs';
import { Turma } from '../../core/models/turma';
import { TurmaService } from '../../core/services/turma.service';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog.component';
import { BrasiliaDatePipe } from '../../shared/pipes/brasilia-date.pipe';

@Component({
  selector: 'app-turma-list',
  standalone: true,
  imports: [
    RouterLink,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatSnackBarModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    BrasiliaDatePipe
  ],
  templateUrl: './turma-list.component.html',
  styleUrl: './turma-list.component.scss'
})
export class TurmaListComponent implements OnInit {
  private readonly turmaService = inject(TurmaService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  turmas: Turma[] = [];
  loading = true;
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
    this.carregar();
  }

  carregar(): void {
    this.loading = true;
    this.turmaService.listar().subscribe({
      next: (turmas) => {
        this.turmas = turmas;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
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
