import { TestBed } from '@angular/core/testing';
import { GameLog } from '../game-log';
import { GameResult } from '../../../../../core/models/game-result.model';

describe('GameLog', () => {
  const result: GameResult = {
    gameStateDto: {
      gameId: 'g1',
      lives: 3,
      gold: 10,
      level: 0,
      score: 5,
      turn: 2,
      reachedGoal: false,
    },
    log: [],
  };

  it('exposes the provided result and emits on dismiss', () => {
    TestBed.configureTestingModule({ imports: [GameLog] });
    TestBed.overrideComponent(GameLog, { set: { template: '', imports: [] } });
    const fixture = TestBed.createComponent(GameLog);
    fixture.componentRef.setInput('result', result);
    const cmp = fixture.componentInstance;

    expect(cmp.result()).toEqual(result);

    let closed = false;
    cmp.closed.subscribe(() => (closed = true));
    cmp['dismiss']();
    expect(closed).toBe(true);
  });
});
