import { Component, input, output, signal } from '@angular/core';

@Component({
  selector: 'app-start-screen',
  templateUrl: './start-screen.html',
})
export class StartScreen {
  strategies = input<string[]>([]);
  startGame = output<string | null>();

  selected = signal<string | null>(null);

  isSelected(strategy: string | null): boolean {
    return this.selected() === strategy;
  }

  choose(strategy: string | null): void {
    this.selected.set(strategy);
  }

  emitStart(): void {
    this.startGame.emit(this.selected());
  }
}
