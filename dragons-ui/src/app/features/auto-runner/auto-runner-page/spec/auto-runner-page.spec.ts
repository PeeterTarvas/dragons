import { TestBed } from '@angular/core/testing';
import { of, Subject } from 'rxjs';
import type { Mock } from 'vitest';
import { GameResult } from '../../../../core/models/game-result.model';
import { GameStore } from '../../../../core/services/game-store';
import { AutoGame } from '../../../../core/services/auto-game';
import { AutoRunnerPage } from '../auto-runner-page';


function makeResult(score: number, logLen = 1): GameResult {
  return {
    gameStateDto: {
      gameId: 'g1',
      lives: 3,
      gold: 40,
      level: 1,
      score,
      turn: 2,
      reachedGoal: score >= 1000,
    },
    log: Array.from({ length: logLen }, (_unused, i) => ({
      turn: i + 1,
      message: 'm' + i,
      probability: 'Sure thing',
      success: true,
      score,
      lives: 3,
      gold: 40,
    })),
  };
}

describe('AutoRunnerPage', () => {
  let stream$: Subject<GameResult>;
  let auto: { strategies: () => unknown; stream: Mock };
  let store: GameStore;
  let cmp: AutoRunnerPage;

  beforeEach(() => {
    stream$ = new Subject<GameResult>();
    auto = {
      strategies: () => of(['expected-value', 'low-risk']),
      stream: vi.fn(() => stream$.asObservable()),
    };

    TestBed.configureTestingModule({
      imports: [AutoRunnerPage],
      providers: [{ provide: AutoGame, useValue: auto }, GameStore],
    });
    TestBed.overrideComponent(AutoRunnerPage, { set: { template: '', imports: [] } });
    cmp = TestBed.createComponent(AutoRunnerPage).componentInstance;
    store = TestBed.inject(GameStore);
  });

  it('starts a run and applies streamed state to the store', () => {
    cmp['onStart']('expected-value');
    expect(auto.stream).toHaveBeenCalledWith('expected-value');
    expect(cmp['running']()).toBe(true);
    expect(cmp['started']()).toBe(true);
    expect(store.selectedStrategy()).toBe('expected-value');

    stream$.next(makeResult(250));
    expect(store.score()).toBe(250);
    expect(cmp['result']()).not.toBeNull();

    stream$.complete();
    expect(cmp['running']()).toBe(false);
    expect(cmp['finished']()).toBe(true);
  });

  it('passes undefined to the stream when no strategy is chosen', () => {
    cmp['onStart'](null);
    expect(auto.stream).toHaveBeenCalledWith(undefined);
  });

  it('surfaces a stream error and returns to the picker', () => {
    cmp['onStart']('low-risk');
    stream$.error(new Error('boom'));
    expect(store.error()).toBe('boom');
    expect(cmp['running']()).toBe(false);
    expect(cmp['started']()).toBe(false);
  });

  it('exposes the turn log newest-first', () => {
    cmp['onStart']('low-risk');
    stream$.next(makeResult(300, 3));
    const turns = cmp['turns']();
    expect(turns.length).toBe(3);
    expect(turns[0].turn).toBe(3);
    expect(turns[2].turn).toBe(1);
  });

  it('stop ends the run; newRun resets back to the picker', () => {
    cmp['onStart']('low-risk');
    cmp['stop']();
    expect(cmp['running']()).toBe(false);
    expect(cmp['finished']()).toBe(true);

    cmp['onStart']('low-risk');
    stream$.next(makeResult(100));
    cmp['newRun']();
    expect(cmp['started']()).toBe(false);
    expect(cmp['result']()).toBeNull();
    expect(store.hasGame()).toBe(false);
  });
});
