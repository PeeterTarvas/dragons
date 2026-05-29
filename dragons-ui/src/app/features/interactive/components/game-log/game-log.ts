import {Component, input, output} from '@angular/core';
import {GameResult} from '../../../../core/models/game-result.model';

@Component({
  selector: 'app-game-log',
  imports: [],
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
