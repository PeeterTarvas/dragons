import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { BatchStats } from '../models/batch-stats.model';
import { GameResult } from '../models/game-result.model';
import {environment} from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AutoGame {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiUrl;

  play(strategy?: string): Observable<GameResult> {
    let params = new HttpParams();
    if (strategy) {
      params = params.set('strategy', strategy);
    }
    return this.http.post<GameResult>(`${this.base}/play`, null, { params });
  }

  playBatch(games: number, strategy?: string): Observable<BatchStats> {
    let params = new HttpParams().set('games', games);
    if (strategy) {
      params = params.set('strategy', strategy);
    }
    return this.http.post<BatchStats>(`${this.base}/play/batch`, null, { params });
  }

  strategies(): Observable<string[]> {
    return this.http.get<string[]>(`${this.base}/strategies`);
  }

  stream(strategy?: string): Observable<GameResult> {
    return new Observable<GameResult>((subscriber) => {
      if (typeof EventSource === 'undefined') {
        subscriber.error(new Error('EventSource is unavailable in this environment'));
        return;
      }
      const url = strategy
        ? `${this.base}/stream?strategy=${encodeURIComponent(strategy)}`
        : `${this.base}/stream`;
      const source = new EventSource(url);
      source.onmessage = (event) => {
        try {
          subscriber.next(JSON.parse(event.data) as GameResult);
        } catch {
          subscriber.error(new Error('Malformed SSE payload'));
        }
      };
      // The server closes the stream by emitting an error event when done.
      source.onerror = () => {
        source.close();
        subscriber.complete();
      };
      return () => source.close();
    });
  }
}
