import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'brasiliaDate',
  standalone: true
})
export class BrasiliaDatePipe implements PipeTransform {
  transform(value: string | null | undefined, withTime = true): string {
    if (!value) {
      return '—';
    }

    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return value;
    }

    const options: Intl.DateTimeFormatOptions = withTime
      ? {
          day: '2-digit',
          month: '2-digit',
          year: 'numeric',
          hour: '2-digit',
          minute: '2-digit',
          timeZone: 'America/Sao_Paulo'
        }
      : {
          day: '2-digit',
          month: '2-digit',
          year: 'numeric',
          timeZone: 'America/Sao_Paulo'
        };

    return new Intl.DateTimeFormat('pt-BR', options).format(date);
  }
}
