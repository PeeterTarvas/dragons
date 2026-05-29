import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { ProblemDetail } from '../models/problem-detail.model';
import { GameStoreService } from '../services/game-store.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const store = inject(GameStoreService);
  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      store.setError(toMessage(err));
      return throwError(() => err);
    }),
  );
};

function toMessage(err: HttpErrorResponse): string {
  const problem = err.error as ProblemDetail | string | null;
  if (problem && typeof problem === 'object') {
    if (problem.violations?.length) {
      return problem.violations.map((v) => `${v.field}: ${v.message}`).join('; ');
    }
    if (problem.detail) {
      return problem.detail;
    }
    if (problem.title) {
      return problem.title;
    }
  }
  if (err.status === 0) {
    return 'Cannot reach the server. Is the backend running?';
  }
  return err.message || 'An unexpected error occurred.';
}
