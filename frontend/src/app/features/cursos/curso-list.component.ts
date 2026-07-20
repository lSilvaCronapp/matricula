import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { filter, switchMap } from 'rxjs';
import { Curso } from '../../core/models/curso';
import { CursoService } from '../../core/services/curso.service';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog.component';
import { BrasiliaDatePipe } from '../../shared/pipes/brasilia-date.pipe';

@Component({
  selector: 'app-curso-list',
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
  templateUrl: './curso-list.component.html',
  styleUrl: './curso-list.component.scss'
})
export class CursoListComponent implements OnInit {
  private readonly cursoService = inject(CursoService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  cursos: Curso[] = [];
  loading = true;
  displayedColumns = ['codigo', 'nome', 'ativo', 'updatedAt', 'acoes'];

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.loading = true;
    this.cursoService.listar().subscribe({
      next: (cursos) => {
        this.cursos = cursos;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  excluir(curso: Curso): void {
    this.dialog
      .open(ConfirmDialogComponent, {
        data: {
          title: 'Excluir curso',
          message: `Deseja excluir o curso "${curso.nome}"?`
        }
      })
      .afterClosed()
      .pipe(
        filter(Boolean),
        switchMap(() => this.cursoService.excluir(curso.id))
      )
      .subscribe({
        next: () => {
          this.snackBar.open('Curso excluído.', 'Fechar', { duration: 3000 });
          this.carregar();
        }
      });
  }
}
