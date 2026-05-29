import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'interactive' },
  {
    path: 'interactive',
    loadComponent: () =>
      import('./features/interactive/interactive-page/interactive-page').then(
        (m) => m.InteractivePage,
      ),
  },
  {
    path: 'auto-runner',
    loadComponent: () =>
      import('./features/auto-runner/auto-runner-page/auto-runner-page').then(
        (m) => m.AutoRunnerPage,
      ),
  },
  { path: '**', redirectTo: 'interactive' },
];
