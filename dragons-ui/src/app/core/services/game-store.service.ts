import { Injectable, computed, signal } from '@angular/core';
import { Message } from '../models/message.model';
import { Board } from '../models/board.model';
import { BuyResponse } from '../models/buy-response.model';
import { GameState } from '../models/game-state.model';
import { ShopItem } from '../models/shop-item.model';
import { SolveResponse } from '../models/solve-response.model';
import {GameResult} from '../models/game-result.model';

@Injectable({ providedIn: 'root' })
export class GameStoreService {
  readonly gameId = signal<string | null>(null);
  readonly lives = signal<number>(0);
  readonly gold = signal<number>(0);
  readonly score = signal<number>(0);
  readonly level = signal<number>(0);
  readonly turn = signal<number>(0);
  readonly ads = signal<Message[]>([]);
  readonly shopItems = signal<ShopItem[]>([]);
  readonly recommendedAdId = signal<string | null>(null);
  readonly error = signal<string | null>(null);
  readonly loading = signal<boolean>(false);

  readonly isAlive = computed(() => this.lives() > 0);
  readonly hasGame = computed(() => this.gameId() !== null);
  readonly reachedGoal = computed(() => this.score() >= 1000);

  startGame(state: GameState): void {
    this.gameId.set(state.gameId);
    this.applyGameState(state);
    this.ads.set([]);
    this.shopItems.set([]);
    this.recommendedAdId.set(null);
    this.error.set(null);
  }

  applyGameState(state: GameState): void {
    this.lives.set(state.lives);
    this.gold.set(state.gold);
    this.level.set(state.level);
    this.score.set(state.score);
    this.turn.set(state.turn);
  }

  applyGameResult(result: GameResult): void {
    this.applyGameState(result.gameStateDto);
  }

  setBoard(board: Board): void {
    this.ads.set(board.ads);
    this.recommendedAdId.set(board.recommendedAdId);
  }

  setShop(items: ShopItem[]): void {
    this.shopItems.set(items);
  }

  applySolveResponse(res: SolveResponse): void {
    this.lives.set(res.lives);
    this.gold.set(res.gold);
    this.score.set(res.score);
    this.turn.set(res.turn);
  }

  applyBuyResponse(res: BuyResponse): void {
    this.gold.set(res.gold);
    this.lives.set(res.lives);
    this.level.set(res.level);
    this.turn.set(res.turn);
  }

  setError(message: string | null): void {
    this.error.set(message);
  }

  setLoading(value: boolean): void {
    this.loading.set(value);
  }

  reset(): void {
    this.gameId.set(null);
    this.lives.set(0);
    this.gold.set(0);
    this.score.set(0);
    this.level.set(0);
    this.turn.set(0);
    this.ads.set([]);
    this.shopItems.set([]);
    this.recommendedAdId.set(null);
    this.error.set(null);
    this.loading.set(false);
  }
}
