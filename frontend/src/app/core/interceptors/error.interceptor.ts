import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, throwError } from 'rxjs';
import { ErrorResponse } from '../models/error-response';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const snackBar = inject(MatSnackBar);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      const body = error.error as ErrorResponse | null;
      const status = error.status;

      if (status === 400) {
        // Field errors are handled by forms; only show generic if no details.
        if (!body?.details?.length) {
          snackBar.open(body?.message || 'Dados inválidos.', 'Fechar', { duration: 5000 });
        }
      } else if (status === 404) {
        snackBar.open(body?.message || 'Recurso não encontrado.', 'Fechar', { duration: 5000 });
      } else if (status === 409) {
        snackBar.open(body?.message || 'Conflito de regra de negócio.', 'Fechar', { duration: 6000 });
      } else if (status === 0 || status >= 500) {
        snackBar.open('Erro inesperado. Tente novamente.', 'Fechar', { duration: 6000 });
      } else {
        snackBar.open(body?.message || 'Não foi possível concluir a operação.', 'Fechar', {
          duration: 5000
        });
      }

      return throwError(() => error);
    })
  );
};
