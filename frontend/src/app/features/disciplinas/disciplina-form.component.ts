import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Curso } from '../../core/models/curso';
import { CursoService } from '../../core/services/curso.service';
import { DisciplinaService } from '../../core/services/disciplina.service';
import { applyServerFieldErrors } from '../../shared/utils/form-error.util';

@Component({
  selector: 'app-disciplina-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    MatSnackBarModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './disciplina-form.component.html',
  styleUrl: './disciplina-form.component.scss'
})
export class DisciplinaFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly disciplinaService = inject(DisciplinaService);
  private readonly cursoService = inject(CursoService);
  private readonly snackBar = inject(MatSnackBar);

  readonly form = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(120)]],
    codigo: ['', [Validators.required, Validators.maxLength(30)]],
    cargaHoraria: [60, [Validators.required, Validators.min(1)]],
    cursoId: ['', Validators.required],
    ativo: [true, Validators.required]
  });

  cursos: Curso[] = [];
  id: string | null = null;
  loading = false;
  saving = false;

  get isEdit(): boolean {
    return !!this.id;
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');
    this.cursoService.listar({ page: 0, size: 100 }).subscribe({
      next: (page) => {
        this.cursos = page.content;
      }
    });

    if (this.id) {
      this.loading = true;
      this.disciplinaService.buscarPorId(this.id).subscribe({
        next: (disciplina) => {
          this.form.patchValue({
            nome: disciplina.nome,
            codigo: disciplina.codigo,
            cargaHoraria: disciplina.cargaHoraria,
            cursoId: disciplina.cursoId,
            ativo: disciplina.ativo
          });
          this.loading = false;
        },
        error: () => {
          this.loading = false;
          void this.router.navigate(['/disciplinas']);
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
      nome: raw.nome.trim(),
      codigo: raw.codigo.trim(),
      cargaHoraria: Number(raw.cargaHoraria),
      cursoId: raw.cursoId,
      ativo: raw.ativo
    };

    this.saving = true;
    const request$ = this.id
      ? this.disciplinaService.atualizar(this.id, request)
      : this.disciplinaService.criar(request);

    request$.subscribe({
      next: () => {
        this.snackBar.open(this.id ? 'Disciplina atualizada.' : 'Disciplina criada.', 'Fechar', {
          duration: 3000
        });
        void this.router.navigate(['/disciplinas']);
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
      return 'Deve ser maior que zero';
    }
    return 'Valor inválido';
  }
}
