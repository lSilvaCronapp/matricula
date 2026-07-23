import { HttpErrorResponse } from '@angular/common/http';
import { MonoTypeOperatorFunction, retry, timer } from 'rxjs';

/** Retries transient network / 5xx failures (useful while backend is warming up). */
export function retryTransient<T>(count = 4, delayMs = 800): MonoTypeOperatorFunction<T> {
  return retry({
    count,
    delay: (error: unknown, retryIndex) => {
      const status = error instanceof HttpErrorResponse ? error.status : undefined;
      if (status !== undefined && status !== 0 && status < 500) {
        throw error;
      }
      return timer(delayMs * retryIndex);
    }
  });
}
