import { Component, computed, input, output, signal } from '@angular/core';
import { TranslocoPipe } from '@ngneat/transloco';

@Component({
  selector: 'app-start-screen',
  templateUrl: './start-screen.html',
  imports: [TranslocoPipe],
})
export class StartScreen {
  readonly strategies = input<string[]>([]);
  readonly allowNone = input<boolean>(true);

  readonly startGame = output<string | null>();

  readonly selected = signal<string | null>(null);

  readonly canStart = computed(() => this.allowNone() || this.selected() !== null);

  isSelected(strategy: string | null): boolean {
    return this.selected() === strategy;
  }

  choose(strategy: string | null): void {
    this.selected.set(strategy);
  }

  emitStart(): void {
    if (!this.canStart()) {
      return;
    }
    this.startGame.emit(this.selected());
  }
}
