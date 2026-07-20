import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AlunoService } from '../../core/services/aluno.service';
import { CpfMaskDirective } from '../../shared/directives/cpf-mask.directive';
import { applyServerFieldErrors, onlyDigits } from '../../shared/utils/form-error.util';

@Component({
  selector: 'app-aluno-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatCheckboxModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    CpfMaskDirective
  ],
  templateUrl: './aluno-form.component.html',
  styleUrl: './aluno-form.component.scss'
})
export class AlunoFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly alunoService = inject(AlunoService);
  private readonly snackBar = inject(MatSnackBar);

  readonly form = this.fb.nonNullable.group({
    nome: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(120)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
    cpf: ['', [Validators.required, Validators.pattern(/^\d{11}$/)]],
    matriculaAcademica: ['', [Validators.required, Validators.maxLength(30)]],
    dataNascimento: [''],
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
      this.alunoService.buscarPorId(this.id).subscribe({
        next: (aluno) => {
          this.form.patchValue({
            nome: aluno.nome,
            email: aluno.email,
            cpf: aluno.cpf,
            matriculaAcademica: aluno.matriculaAcademica,
            dataNascimento: aluno.dataNascimento ?? '',
            ativo: aluno.ativo
          });
          this.loading = false;
        },
        error: () => {
          this.loading = false;
          void this.router.navigate(['/alunos']);
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
      email: raw.email.trim(),
      cpf: onlyDigits(raw.cpf),
      matriculaAcademica: raw.matriculaAcademica.trim(),
      dataNascimento: raw.dataNascimento || null,
      ativo: raw.ativo
    };

    this.saving = true;
    const request$ = this.id
      ? this.alunoService.atualizar(this.id, request)
      : this.alunoService.criar(request);

    request$.subscribe({
      next: () => {
        this.snackBar.open(this.id ? 'Aluno atualizado.' : 'Aluno criado.', 'Fechar', {
          duration: 3000
        });
        void this.router.navigate(['/alunos']);
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
    if (control.errors['email']) {
      return 'E-mail inválido';
    }
    if (control.errors['pattern']) {
      return 'CPF deve conter 11 dígitos';
    }
    if (control.errors['minlength'] || control.errors['maxlength']) {
      return 'Tamanho inválido';
    }
    return 'Valor inválido';
  }
}
