import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import type { Mock } from 'vitest';
import { GameState } from '../../../../core/models/game-state.model';
import { Message } from '../../../../core/models/message.model';
import { Board } from '../../../../core/models/board.model';
import { ShopItem } from '../../../../core/models/shop-item.model';
import { GameStore } from '../../../../core/services/game-store';
import { SolveResponse } from '../../../../core/models/solve-response.model';
import { BuyResponse } from '../../../../core/models/buy-response.model';
import { GameResult } from '../../../../core/models/game-result.model';
import { InteractiveGame } from '../../../../core/services/interactive-game';
import { AutoGame } from '../../../../core/services/auto-game';
import { InteractivePage } from '../interactive-page';


const ad: Message = {
  adId: 'ad-1',
  message: 'do it',
  reward: 10,
  expiresIn: 3,
  probability: 'Sure thing',
  encrypted: 0,
  estimatedSuccess: 0.9,
};
const state: GameState = {
  gameId: 'g1',
  lives: 3,
  gold: 100,
  level: 0,
  score: 0,
  turn: 0,
  reachedGoal: false,
};
const board: Board = { ads: [ad], recommendedAdId: 'ad-1' };
const shopItems: ShopItem[] = [{ id: 'item-1', name: 'Healing potion', cost: 50 }];

describe('InteractivePage', () => {
  let interactive: {
    start: Mock;
    getBoard: Mock;
    getShop: Mock;
    solve: Mock;
    buy: Mock;
    state: Mock;
  };
  let store: GameStore;
  let cmp: InteractivePage;

  beforeEach(() => {
    const solveRes: SolveResponse = {
      success: true,
      lives: 3,
      gold: 110,
      score: 50,
      highScore: 50,
      turn: 1,
      message: 'ok',
    };
    const buyRes: BuyResponse = { shoppingSuccess: true, gold: 50, lives: 4, level: 0, turn: 2 };
    const gameResult: GameResult = { gameStateDto: state, log: [] };

    interactive = {
      start: vi.fn(() => of(state)),
      getBoard: vi.fn(() => of(board)),
      getShop: vi.fn(() => of(shopItems)),
      solve: vi.fn(() => of(solveRes)),
      buy: vi.fn(() => of(buyRes)),
      state: vi.fn(() => of(gameResult)),
    };

    TestBed.configureTestingModule({
      imports: [InteractivePage],
      providers: [
        { provide: InteractiveGame, useValue: interactive },
        { provide: AutoGame, useValue: { strategies: () => of([]) } },
        GameStore,
      ],
    });
    TestBed.overrideComponent(InteractivePage, { set: { template: '', imports: [] } });
    cmp = TestBed.createComponent(InteractivePage).componentInstance;
    store = TestBed.inject(GameStore);
  });

  it('starts a game and loads the board + shop', () => {
    cmp['onStart']('low-risk');
    expect(interactive.start).toHaveBeenCalled();
    expect(store.gameId()).toBe('g1');
    expect(store.ads()).toEqual([ad]);
    expect(store.shopItems()).toEqual(shopItems);
    expect(store.selectedStrategy()).toBe('low-risk');
    expect(store.loading()).toBe(false);
  });

  it('solves the selected ad', () => {
    store.startGame(state);
    store.setBoard(board);
    cmp['form'].controls.selectedAdId.setValue('ad-1');

    cmp['solveSelected']();

    expect(interactive.solve).toHaveBeenCalledWith('g1', ad);
    expect(store.score()).toBe(50);
  });

  it('does nothing on solve when no ad is selected', () => {
    store.startGame(state);
    cmp['solveSelected']();
    expect(interactive.solve).not.toHaveBeenCalled();
  });

  it('blocks buying when the item is unaffordable and surfaces an error', () => {
    store.startGame({ ...state, gold: 10 });
    store.setShop(shopItems);
    cmp['form'].controls.selectedItemId.setValue('item-1');

    cmp['buySelected']();

    expect(interactive.buy).not.toHaveBeenCalled();
    expect(store.error()).toContain('Not enough gold');
    expect(cmp['form'].controls.selectedItemId.hasError('insufficientGold')).toBe(true);
  });

  it('buys an affordable item', () => {
    store.startGame(state);
    store.setShop(shopItems);
    cmp['form'].controls.selectedItemId.setValue('item-1');

    cmp['buySelected']();

    expect(interactive.buy).toHaveBeenCalledWith('g1', shopItems[0]);
    expect(store.lives()).toBe(4);
  });

  it('flags the recommended ad', () => {
    store.setBoard(board);
    expect(cmp['isRecommended']('ad-1')).toBe(true);
    expect(cmp['isRecommended']('ad-2')).toBe(false);
  });

  it('computes gameOver when a game has run out of lives', () => {
    expect(cmp['gameOver']()).toBe(false);
    store.startGame({ ...state, lives: 0 });
    expect(cmp['gameOver']()).toBe(true);
  });

  it('opens and closes the game log', () => {
    store.startGame(state);
    cmp['viewLog']();
    expect(interactive.state).toHaveBeenCalledWith('g1');
    expect(cmp['showLog']()).toBe(true);
    cmp['closeLog']();
    expect(cmp['showLog']()).toBe(false);
  });

  it('newGame resets store and view state', () => {
    store.startGame(state);
    cmp['newGame']();
    expect(store.hasGame()).toBe(false);
    expect(cmp['showLog']()).toBe(false);
  });
});
