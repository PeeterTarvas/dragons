import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AutoGameService } from '../auto-game.service';

describe('AutoGameService', () => {
  let service: AutoGameService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AutoGameService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('plays a batch with games and strategy params', () => {
    service.playBatch(50, 'expected-value').subscribe();
    const req = httpMock.expectOne((r) => r.url === '/api/play/batch');
    expect(req.request.method).toBe('POST');
    expect(req.request.params.get('games')).toBe('50');
    expect(req.request.params.get('strategy')).toBe('expected-value');
    req.flush({
      games: 50, averageScore: 1, maxScore: 2, minScore: 0,
      gamesReachedTarget: 10, reachedTargetPercent: 20,
    });
  });

  it('lists strategies', () => {
    let result: string[] | undefined;
    service.strategies().subscribe((r) => (result = r));
    const req = httpMock.expectOne('/api/strategies');
    expect(req.request.method).toBe('GET');
    req.flush(['expected-value', 'low-risk']);
    expect(result).toEqual(['expected-value', 'low-risk']);
  });
});
