import {Component, inject} from '@angular/core';
import {StatBadge} from '../../../../shared/components/stat-badge/stat-badge';
import {GameStoreService} from '../../../../core/services/game-store.service';

@Component({
  selector: 'app-player-stats',
  imports: [StatBadge],
  templateUrl: './player-stats.html',
})
export class PlayerStats {
  store = inject(GameStoreService);
}
