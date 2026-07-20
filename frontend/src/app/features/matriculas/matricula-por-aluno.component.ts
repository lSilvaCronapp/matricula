import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { filter, switchMap } from 'rxjs';
import { Aluno } from '../../core/models/aluno';
import { Matricula } from '../../core/models/matricula';
import { AlunoService } from '../../core/services/aluno.service';
import { MatriculaService } from '../../core/services/matricula.service';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog.component';
import { BrasiliaDatePipe } from '../../shared/pipes/brasilia-date.pipe';

@Component({
  selector: 'app-matricula-por-aluno',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSelectModule,
    MatTableModule,
    MatIconModule,
    MatSnackBarModule,
    MatChipsModule,
    MatDialogModule,
    BrasiliaDatePipe
  ],
  templateUrl: './matricula-por-aluno.component.html',
  styleUrl: './matricula-por-aluno.component.scss'
})
export class MatriculaPorAlunoComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly alunoService = inject(AlunoService);
  private readonly matriculaService = inject(MatriculaService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly dialog = inject(MatDialog);

  readonly form = this.fb.nonNullable.group({
    alunoId: ['', Validators.required]
  });

  alunos: Aluno[] = [];
  matriculas: Matricula[] = [];
  loading = false;
  consulted = false;
  displayedColumns = [
    'turmaCodigo',
    'disciplinaNome',
    'status',
    'dataSolicitacao',
    'dataConfirmacao',
    'acoes'
  ];

  ngOnInit(): void {
    this.alunoService.listar().subscribe({
      next: (alunos) => {
        this.alunos = alunos;
      }
    });
  }

  consultar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.consulted = true;
    this.matriculaService.listarPorAluno(this.form.controls.alunoId.value).subscribe({
      next: (matriculas) => {
        this.matriculas = matriculas;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  confirmar(matricula: Matricula): void {
    this.matriculaService.confirmar(matricula.id).subscribe({
      next: () => {
        this.snackBar.open('Matrícula confirmada.', 'Fechar', { duration: 3000 });
        this.consultar();
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
          this.consultar();
        }
      });
  }
}
