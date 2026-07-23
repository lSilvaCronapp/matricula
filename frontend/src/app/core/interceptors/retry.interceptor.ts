import { HttpInterceptorFn } from '@angular/common/http';
import { retryTransient } from '../utils/retry-transient.util';

/** Retries transient failures on safe GET requests before the error interceptor runs. */
export const retryInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.method !== 'GET') {
    return next(req);
  }
  return next(req).pipe(retryTransient());
};
