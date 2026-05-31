import { Component, DestroyRef, afterNextRender, inject, signal, computed } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslocoPipe } from '@ngneat/transloco';
import { Subscription } from 'rxjs';

import { AutoGame } from '../../../core/services/auto-game';
import { GameStore } from '../../../core/services/game-store';
import { GameResult } from '../../../core/models/game-result.model';
import { StartScreen } from '../../../shared/components/start-screen/start-screen';
import { ErrorBanner } from '../../../shared/components/error-banner/error-banner';
import { PlayerStats } from '../../interactive/components/player-stats/player-stats';

@Component({
  selector: 'app-auto-runner-page',
  imports: [RouterLink, TranslocoPipe, StartScreen, ErrorBanner, PlayerStats],
  templateUrl: './auto-runner-page.html',
  styleUrl: './auto-runner-page.css',
})
export class AutoRunnerPage {
  protected readonly store = inject(GameStore);
  private readonly auto = inject(AutoGame);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly strategies = signal<string[]>([]);
  protected readonly started = signal<boolean>(false);
  protected readonly running = signal<boolean>(false);
  protected readonly finished = signal<boolean>(false);
  protected readonly result = signal<GameResult | null>(null);

  private streamSub?: Subscription;

  constructor() {
    afterNextRender(() => {
      this.auto.strategies().subscribe({
        next: (list) => this.strategies.set(list),
        error: () => undefined,
      });
    });
    this.destroyRef.onDestroy(() => this.streamSub?.unsubscribe());
  }

  protected readonly turns = computed(() => {
    const current = this.result();
    return current ? [...current.log].reverse() : [];
  });

  protected onStart(strategy: string | null): void {
    this.streamSub?.unsubscribe();
    this.store.reset();
    this.store.selectedStrategy.set(strategy);
    this.result.set(null);
    this.started.set(true);
    this.finished.set(false);
    this.running.set(true);

    this.streamSub = this.auto.stream(strategy ?? undefined).subscribe({
      next: (res) => {
        this.store.applyGameResult(res);
        this.result.set(res);
      },
      error: (err: unknown) => {
        this.store.setError(
          err instanceof Error && err.message
            ? err.message
            : 'The automatic run was interrupted. Please try again.',
        );
        this.running.set(false);
        this.finished.set(false);
        this.started.set(false);
      },
      complete: () => {
        this.running.set(false);
        this.finished.set(true);
      },
    });
  }

  protected stop(): void {
    this.streamSub?.unsubscribe();
    this.running.set(false);
    this.finished.set(true);
    this.store.setLoading(false);
  }

  protected newRun(): void {
    this.streamSub?.unsubscribe();
    this.store.reset();
    this.result.set(null);
    this.started.set(false);
    this.running.set(false);
    this.finished.set(false);
  }
}
