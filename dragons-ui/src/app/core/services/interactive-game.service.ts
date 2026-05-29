import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Board } from '../models/board.model';
import { BuyResponse } from '../models/buy-response.model';
import { GameState } from '../models/game-state.model';
import { ShopItem } from '../models/shop-item.model';
import { SolveResponse } from '../models/solve-response.model';
import {Message} from '../models/message.model';
import {GameResult} from '../models/game-result.model';

@Injectable({ providedIn: 'root' })
export class InteractiveGameService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/games';

  start(): Observable<GameState> {
    return this.http.get<GameState>(`${this.base}/play`);
  }

  getBoard(gameId: string, strategy?: string): Observable<Board> {
    let params = new HttpParams();
    if (strategy) {
      params = params.set('strategy', strategy);
    }
    return this.http.get<Board>(`${this.base}/${encodeURIComponent(gameId)}/board`, { params });
  }

  solve(gameId: string, message: Message): Observable<SolveResponse> {
    return this.http.post<SolveResponse>(`${this.base}/${encodeURIComponent(gameId)}/solve`, message);
  }

  getShop(gameId: string): Observable<ShopItem[]> {
    return this.http.get<ShopItem[]>(`${this.base}/${encodeURIComponent(gameId)}/shop`);
  }

  buy(gameId: string, item: ShopItem): Observable<BuyResponse> {
    return this.http.post<BuyResponse>(`${this.base}/${encodeURIComponent(gameId)}/buy`, item);
  }

  state(gameId: string): Observable<GameResult> {
    return this.http.get<GameResult>(`${this.base}/${encodeURIComponent(gameId)}/state`);
  }
}
