import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { filter, switchMap } from 'rxjs';
import { Disciplina } from '../../core/models/disciplina';
import { DisciplinaService } from '../../core/services/disciplina.service';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog.component';
import { BrasiliaDatePipe } from '../../shared/pipes/brasilia-date.pipe';

@Component({
  selector: 'app-disciplina-list',
  standalone: true,
  imports: [
    RouterLink,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatSnackBarModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    BrasiliaDatePipe
  ],
  templateUrl: './disciplina-list.component.html',
  styleUrl: './disciplina-list.component.scss'
})
export class DisciplinaListComponent implements OnInit {
  private readonly disciplinaService = inject(DisciplinaService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  disciplinas: Disciplina[] = [];
  loading = true;
  displayedColumns = ['codigo', 'nome', 'cursoNome', 'cargaHoraria', 'ativo', 'updatedAt', 'acoes'];

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.loading = true;
    this.disciplinaService.listar().subscribe({
      next: (disciplinas) => {
        this.disciplinas = disciplinas;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
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
