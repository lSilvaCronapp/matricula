import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Aluno } from '../../core/models/aluno';
import { AlunoService } from '../../core/services/aluno.service';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog.component';
import { BrasiliaDatePipe } from '../../shared/pipes/brasilia-date.pipe';
import { filter, switchMap } from 'rxjs';

@Component({
  selector: 'app-aluno-list',
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
  templateUrl: './aluno-list.component.html',
  styleUrl: './aluno-list.component.scss'
})
export class AlunoListComponent implements OnInit {
  private readonly alunoService = inject(AlunoService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  alunos: Aluno[] = [];
  loading = true;
  displayedColumns = ['nome', 'email', 'cpf', 'matriculaAcademica', 'ativo', 'updatedAt', 'acoes'];

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.loading = true;
    this.alunoService.listar().subscribe({
      next: (alunos) => {
        this.alunos = alunos;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  formatCpf(cpf: string): string {
    const d = cpf.replace(/\D/g, '');
    return d.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
  }

  excluir(aluno: Aluno): void {
    this.dialog
      .open(ConfirmDialogComponent, {
        data: {
          title: 'Excluir aluno',
          message: `Deseja excluir o aluno "${aluno.nome}"?`
        }
      })
      .afterClosed()
      .pipe(
        filter(Boolean),
        switchMap(() => this.alunoService.excluir(aluno.id))
      )
      .subscribe({
        next: () => {
          this.snackBar.open('Aluno excluído.', 'Fechar', { duration: 3000 });
          this.carregar();
        }
      });
  }
}
