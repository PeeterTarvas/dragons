import {Component, inject} from '@angular/core';
import {StatBadge} from '../../../../shared/components/stat-badge/stat-badge';
import {GameStore} from '../../../../core/services/game-store';
import {TranslocoPipe} from '@ngneat/transloco';

@Component({
  selector: 'app-player-stats',
  imports: [StatBadge, TranslocoPipe],
  templateUrl: './player-stats.html',
})
export class PlayerStats {
  store = inject(GameStore);
}
