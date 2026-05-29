import {Component, inject} from '@angular/core';
import {GameStore} from '../../../core/services/game-store';
import {TranslocoPipe} from '@ngneat/transloco';

@Component({
  selector: 'app-error-banner',
  templateUrl: './error-banner.html',
  imports: [TranslocoPipe],
})
export class ErrorBanner {
  store = inject(GameStore);

  dismiss(): void {
    this.store.setError(null);
  }
}
