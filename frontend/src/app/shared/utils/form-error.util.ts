import { HttpErrorResponse } from '@angular/common/http';
import { FormGroup } from '@angular/forms';
import { ErrorResponse } from '../../core/models/error-response';

export function applyServerFieldErrors(form: FormGroup, error: unknown): void {
  if (!(error instanceof HttpErrorResponse) || error.status !== 400) {
    return;
  }

  const body = error.error as ErrorResponse | null;
  const details = body?.details ?? [];

  for (const detail of details) {
    const control = form.get(detail.field);
    if (control) {
      control.setErrors({ server: detail.message });
      control.markAsTouched();
    }
  }
}

export function onlyDigits(value: string | null | undefined): string {
  return (value ?? '').replace(/\D/g, '');
}
