import {Component, inject} from '@angular/core';
import {GameStoreService} from '../../../core/services/game-store.service';

@Component({
  selector: 'app-error-banner',
  templateUrl: './error-banner.html'
})
export class ErrorBanner {
  store = inject(GameStoreService);

  dismiss(): void {
    this.store.setError(null);
  }
}
