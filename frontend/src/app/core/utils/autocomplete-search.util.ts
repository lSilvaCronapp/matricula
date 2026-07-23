import { Observable, debounceTime, distinctUntilChanged, map, merge, of, switchMap } from 'rxjs';

/**
 * Loads options immediately on subscribe, then debounces further typing.
 * Avoids empty autocomplete panels on first open.
 */
export function autocompleteSearch<TControl, TOption>(
  valueChanges: Observable<TControl>,
  initial: TControl,
  toQuery: (value: TControl) => string,
  search: (q: string | undefined) => Observable<TOption[]>,
  onTextChange?: () => void
): Observable<TOption[]> {
  return merge(of(initial), valueChanges.pipe(debounceTime(300))).pipe(
    map((value) => ({ value, q: toQuery(value) })),
    distinctUntilChanged((a, b) => a.q === b.q && typeof a.value === typeof b.value),
    switchMap(({ value, q }) => {
      if (typeof value === 'string' && onTextChange) {
        onTextChange();
      }
      return search(q || undefined);
    })
  );
}
