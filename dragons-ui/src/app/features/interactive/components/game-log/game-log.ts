import {Component, input, output} from '@angular/core';
import {GameResult} from '../../../../core/models/game-result.model';
import {TranslocoPipe} from '@ngneat/transloco';

@Component({
  selector: 'app-game-log',
  imports: [
    TranslocoPipe
  ],
  templateUrl: './game-log.html',
  styleUrl: './game-log.css',
})
export class GameLog {
  readonly result = input.required<GameResult>();
  readonly closed = output<void>();

  protected dismiss(): void {
    this.closed.emit();
  }
}
