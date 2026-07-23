import { HttpParams } from '@angular/common/http';
import { PageRequest } from '../models/page';

export function toHttpParams(request: PageRequest & Record<string, string | number | undefined | null>): HttpParams {
  let params = new HttpParams();
  Object.entries(request).forEach(([key, value]) => {
    if (value === undefined || value === null || value === '') {
      return;
    }
    params = params.set(key, String(value));
  });
  return params;
}
