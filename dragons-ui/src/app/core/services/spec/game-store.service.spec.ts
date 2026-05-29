import { TestBed } from '@angular/core/testing';
import { GameStore } from '../game-store';
import { GameState } from '../../models/game-state.model';

describe('GameStore', () => {
  let store: GameStore;
  const state: GameState = {
    gameId: 'g1', lives: 3, gold: 100, level: 0, score: 0, turn: 0, reachedGoal: false,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({});
    store = TestBed.inject(GameStore);
  });

  it('hasGame is false until a game starts', () => {
    expect(store.hasGame()).toBe(false);
    store.startGame(state);
    expect(store.hasGame()).toBe(true);
    expect(store.gameId()).toBe('g1');
  });

  it('isAlive reflects lives', () => {
    store.startGame(state);
    expect(store.isAlive()).toBe(true);
    store.applySolveResponse({
      success: false, lives: 0, gold: 100, score: 0, highScore: 0, turn: 1, message: 'died',
    });
    expect(store.isAlive()).toBe(false);
  });

  it('reachedGoal flips at 1000 points', () => {
    store.startGame(state);
    expect(store.reachedGoal()).toBe(false);
    store.applySolveResponse({
      success: true, lives: 3, gold: 100, score: 1000, highScore: 1000, turn: 9, message: 'ok',
    });
    expect(store.reachedGoal()).toBe(true);
  });

  it('reset clears all state', () => {
    store.startGame(state);
    store.setError('boom');
    store.reset();
    expect(store.hasGame()).toBe(false);
    expect(store.error()).toBeNull();
    expect(store.lives()).toBe(0);
  });
});
