import {
  Component,
  afterNextRender,
  computed,
  effect,
  inject,
  signal,
} from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
} from '@angular/forms';
import { RouterLink } from '@angular/router';
import {finalize, forkJoin, Observable, of, switchMap, tap} from 'rxjs';

import { GameStore } from '../../../core/services/game-store';
import { InteractiveGame } from '../../../core/services/interactive-game';
import { AutoGame } from '../../../core/services/auto-game';
import { Message } from '../../../core/models/message.model';
import { PlayerStats } from '../components/player-stats/player-stats';
import { ErrorBanner } from '../../../shared/components/error-banner/error-banner';
import { LoadingSpinner } from '../../../shared/components/loading-spinner/loading-spinner';
import { StartScreen } from '../../../shared/components/start-screen/start-screen';
import {GameResult} from '../../../core/models/game-result.model';
import {GameLog} from '../components/game-log/game-log';
import {TranslocoPipe} from '@ngneat/transloco';


@Component({
  selector: 'app-interactive-page',
  imports: [ReactiveFormsModule, RouterLink, StartScreen, PlayerStats, ErrorBanner, LoadingSpinner, GameLog, TranslocoPipe],
  templateUrl: './interactive-page.html',
  styleUrl: './interactive-page.css',
})
export class InteractivePage {
  protected readonly store = inject(GameStore);
  private readonly interactive = inject(InteractiveGame);
  private readonly auto = inject(AutoGame);

  protected readonly strategies = signal<string[]>([]);

  protected readonly gameResult = signal<GameResult | null>(null);
  protected readonly showLog = signal<boolean>(false);

  protected readonly gameOver = computed(() => this.store.hasGame() && this.store.lives() <= 0);

  protected readonly form = new FormGroup({
    selectedAdId: new FormControl<string | null>(null),
    selectedItemId: new FormControl<string | null>(null, {
      validators: [(c: AbstractControl) => this.affordable(c)],
    }),
  });

  constructor() {
    afterNextRender(() => {
      this.auto.strategies().subscribe({
        next: (list) => this.strategies.set(list),
        error: () => undefined,
      });
    });

    effect(() => {
      this.store.gold();
      this.store.shopItems();
      this.form.controls.selectedItemId.updateValueAndValidity({ emitEvent: false });
    });
  }

  private affordable(control: AbstractControl): ValidationErrors | null {
    const id = control.value as string | null;
    if (!id) {
      return null;
    }
    const item = this.store.shopItems().find((i) => i.id === id);
    if (item && item.cost > this.store.gold()) {
      return { insufficientGold: { cost: item.cost, gold: this.store.gold() } };
    }
    return null;
  }

  protected onStart(strategy: string | null): void {
    this.store.selectedStrategy.set(strategy);
    this.store.setLoading(true);
    this.interactive
      .start()
      .pipe(
        switchMap((state) => {
          this.store.startGame(state);
          return this.loadBoardAndShop(state.gameId);
        }),
        finalize(() => this.store.setLoading(false)),
      )
      .subscribe({ error: () => undefined });
  }

  protected solveSelected(): void {
    const adId = this.form.controls.selectedAdId.value;
    const gameId = this.store.gameId();
    if (!adId || !gameId) {
      return;
    }
    const message: Message | undefined = this.store.ads().find((a) => a.adId === adId);
    if (!message) {
      return;
    }
    this.store.setLoading(true);
    this.interactive
      .solve(gameId, message)
      .pipe(
        switchMap((res) => {
          this.store.applySolveResponse(res);
          return res.lives <= 0 ? of(null) : this.loadBoardAndShop(gameId);
        }),
        finalize(() => this.store.setLoading(false)),
      )
      .subscribe({ error: () => undefined });
  }

  protected buySelected(): void {
    const itemId = this.form.controls.selectedItemId.value;
    const gameId = this.store.gameId();
    if (!itemId || !gameId) {
      return;
    }
    const item = this.store.shopItems().find((i) => i.id === itemId);
    if (!item) {
      return;
    }
    if (item.cost > this.store.gold()) {
      this.form.controls.selectedItemId.setErrors({ insufficientGold: true });
      this.store.setError(
        `Not enough gold to buy "${item.name}" — costs ${item.cost}, you have ${this.store.gold()}.`,
      );
      return;
    }
    this.store.setLoading(true);
    this.interactive
      .buy(gameId, item)
      .pipe(
        switchMap((res) => {
          this.store.applyBuyResponse(res);
          return res.lives <= 0 ? of(null) : this.loadBoardAndShop(gameId);
        }),
        finalize(() => this.store.setLoading(false)),
      )
      .subscribe({ error: () => undefined });
  }

  protected isRecommended(adId: string): boolean {
    return this.store.recommendedAdId() === adId;
  }

  private fetchLog(gameId: string): Observable<GameResult> {
    return this.interactive.state(gameId).pipe(
      tap((result) => {
        this.gameResult.set(result);
        this.showLog.set(true);
      }),
    );
  }

  protected viewLog(): void {
    const gameId = this.store.gameId();
    if (!gameId) {
      return;
    }
    this.store.setLoading(true);
    this.fetchLog(gameId)
      .pipe(finalize(() => this.store.setLoading(false)))
      .subscribe({ error: () => undefined });
  }

  protected closeLog(): void {
    this.showLog.set(false);
  }

  protected newGame(): void {
    this.store.reset();
    this.gameResult.set(null);
    this.showLog.set(false);
    this.form.reset({ selectedAdId: null, selectedItemId: null });
  }

  private loadBoardAndShop(gameId: string) {
    const strategy = this.store.selectedStrategy() ?? undefined;
    return forkJoin({
      board: this.interactive.getBoard(gameId, strategy),
      shop: this.interactive.getShop(gameId),
    }).pipe(
      tap(({ board, shop }) => {
        this.store.setBoard(board);
        this.store.setShop(shop);
        this.form.reset({ selectedAdId: null, selectedItemId: null });
      }),
    );
  }
}
