import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Disciplina } from '../../core/models/disciplina';
import { PERIODO_OPTIONS, STATUS_TURMA_OPTIONS, StatusTurma } from '../../core/models/enums';
import { DisciplinaService } from '../../core/services/disciplina.service';
import { TurmaService } from '../../core/services/turma.service';
import { applyServerFieldErrors } from '../../shared/utils/form-error.util';

@Component({
  selector: 'app-turma-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSnackBarModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './turma-form.component.html',
  styleUrl: './turma-form.component.scss'
})
export class TurmaFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly turmaService = inject(TurmaService);
  private readonly disciplinaService = inject(DisciplinaService);
  private readonly snackBar = inject(MatSnackBar);

  readonly statusOptions = STATUS_TURMA_OPTIONS;
  readonly periodoOptions = PERIODO_OPTIONS;

  readonly form = this.fb.nonNullable.group({
    codigo: ['', [Validators.required, Validators.maxLength(40)]],
    disciplinaId: ['', Validators.required],
    ano: [new Date().getFullYear(), [Validators.required, Validators.min(2000)]],
    periodo: ['1', Validators.required],
    limiteVagas: [40, [Validators.required, Validators.min(1)]],
    status: ['ABERTA' as StatusTurma, Validators.required]
  });

  disciplinas: Disciplina[] = [];
  id: string | null = null;
  vagasOcupadas: number | null = null;
  loading = false;
  saving = false;

  get isEdit(): boolean {
    return !!this.id;
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');
    this.disciplinaService.listar({ page: 0, size: 100 }).subscribe({
      next: (page) => {
        this.disciplinas = page.content;
      }
    });

    if (this.id) {
      this.loading = true;
      this.turmaService.buscarPorId(this.id).subscribe({
        next: (turma) => {
          this.vagasOcupadas = turma.vagasOcupadas;
          this.form.patchValue({
            codigo: turma.codigo,
            disciplinaId: turma.disciplinaId,
            ano: turma.ano,
            periodo: turma.periodo,
            limiteVagas: turma.limiteVagas,
            status: turma.status
          });
          this.loading = false;
        },
        error: () => {
          this.loading = false;
          void this.router.navigate(['/turmas']);
        }
      });
    }
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const request = {
      codigo: raw.codigo.trim(),
      disciplinaId: raw.disciplinaId,
      ano: Number(raw.ano),
      periodo: raw.periodo,
      limiteVagas: Number(raw.limiteVagas),
      status: raw.status
    };

    this.saving = true;
    const request$ = this.id
      ? this.turmaService.atualizar(this.id, request)
      : this.turmaService.criar(request);

    request$.subscribe({
      next: () => {
        this.snackBar.open(this.id ? 'Turma atualizada.' : 'Turma criada.', 'Fechar', {
          duration: 3000
        });
        void this.router.navigate(['/turmas']);
      },
      error: (err) => {
        applyServerFieldErrors(this.form, err);
        this.saving = false;
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
    if (control.errors['min']) {
      return 'Valor mínimo inválido';
    }
    return 'Valor inválido';
  }
}
