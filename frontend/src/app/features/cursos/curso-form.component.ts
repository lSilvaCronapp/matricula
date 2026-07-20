import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CursoService } from '../../core/services/curso.service';
import { applyServerFieldErrors } from '../../shared/utils/form-error.util';

@Component({
  selector: 'app-curso-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatCheckboxModule,
    MatSnackBarModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './curso-form.component.html',
  styleUrl: './curso-form.component.scss'
})
export class CursoFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly cursoService = inject(CursoService);
  private readonly snackBar = inject(MatSnackBar);

  readonly form = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(120)]],
    codigo: ['', [Validators.required, Validators.maxLength(30)]],
    descricao: ['', [Validators.maxLength(500)]],
    ativo: [true, Validators.required]
  });

  id: string | null = null;
  loading = false;
  saving = false;

  get isEdit(): boolean {
    return !!this.id;
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');
    if (this.id) {
      this.loading = true;
      this.cursoService.buscarPorId(this.id).subscribe({
        next: (curso) => {
          this.form.patchValue({
            nome: curso.nome,
            codigo: curso.codigo,
            descricao: curso.descricao ?? '',
            ativo: curso.ativo
          });
          this.loading = false;
        },
        error: () => {
          this.loading = false;
          void this.router.navigate(['/cursos']);
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
      descricao: raw.descricao.trim() || null,
      ativo: raw.ativo
    };

    this.saving = true;
    const request$ = this.id
      ? this.cursoService.atualizar(this.id, request)
      : this.cursoService.criar(request);

    request$.subscribe({
      next: () => {
        this.snackBar.open(this.id ? 'Curso atualizado.' : 'Curso criado.', 'Fechar', {
          duration: 3000
        });
        void this.router.navigate(['/cursos']);
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
    return 'Valor inválido';
  }
}
