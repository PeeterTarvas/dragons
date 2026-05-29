import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { InteractiveGame } from '../interactive-game';
import { GameState } from '../../models/game-state.model';

describe('InteractiveGame', () => {
  let service: InteractiveGame;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(InteractiveGame);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('starts a game via GET /api/games/play', () => {
    const mock: GameState = {
      gameId: 'g1', lives: 3, gold: 0, level: 0, score: 0, turn: 0, reachedGoal: false,
    };
    let result: GameState | undefined;
    service.start().subscribe((r) => (result = r));
    const req = httpMock.expectOne('/api/games/play');
    expect(req.request.method).toBe('GET');
    req.flush(mock);
    expect(result).toEqual(mock);
  });

  it('appends the strategy param to the board request', () => {
    service.getBoard('g1', 'low-risk').subscribe();
    const req = httpMock.expectOne((r) => r.url === '/api/games/g1/board');
    expect(req.request.params.get('strategy')).toBe('low-risk');
    req.flush({ ads: [], recommendedAdId: null });
  });

  it('omits the strategy param when not provided', () => {
    service.getBoard('g1').subscribe();
    const req = httpMock.expectOne('/api/games/g1/board');
    expect(req.request.params.has('strategy')).toBe(false);
    req.flush({ ads: [], recommendedAdId: null });
  });

  it('posts the full ad body to solve', () => {
    const message = {
      adId: 'a1', message: 'm', reward: 10, expiresIn: 3,
      probability: 'Sure thing', encrypted: 0, estimatedSuccess: 0.9,
    };
    service.solve('g1', message).subscribe();
    const req = httpMock.expectOne('/api/games/g1/solve');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(message);
    req.flush({ success: true, lives: 3, gold: 10, score: 10, highScore: 10, turn: 1, message: 'ok' });
  });

  it('posts the shop item to buy', () => {
    const item = { id: 'hpot', name: 'Healing potion', cost: 50 };
    service.buy('g1', item).subscribe();
    const req = httpMock.expectOne('/api/games/g1/buy');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(item);
    req.flush({ shoppingSuccess: true, gold: 0, lives: 4, level: 0, turn: 2 });
  });
});
