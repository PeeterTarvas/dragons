import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { errorInterceptor } from '../error-interceptor';
import { GameStoreService } from '../../services/game-store.service';

describe('errorInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let store: GameStoreService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([errorInterceptor])),
        provideHttpClientTesting(),
      ],
    });
    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
    store = TestBed.inject(GameStoreService);
  });

  afterEach(() => httpMock.verify());

  it('surfaces ProblemDetail.detail to the store', () => {
    http.get('/api/games/play').subscribe({ error: () => undefined });
    httpMock.expectOne('/api/games/play').flush(
      { title: 'Game not found', detail: 'No game with id g1' },
      { status: 404, statusText: 'Not Found' },
    );
    expect(store.error()).toBe('No game with id g1');
  });

  it('joins validation violations', () => {
    http.get('/api/games/play').subscribe({ error: () => undefined });
    httpMock.expectOne('/api/games/play').flush(
      {
        title: 'Invalid request body',
        detail: 'Validation failed',
        violations: [{ field: 'adId', message: 'must not be blank' }],
      },
      { status: 400, statusText: 'Bad Request' },
    );
    expect(store.error()).toBe('adId: must not be blank');
  });

  it('reports a connection failure when status is 0', () => {
    http.get('/api/games/play').subscribe({ error: () => undefined });
    httpMock
      .expectOne('/api/games/play')
      .error(new ProgressEvent('error'), { status: 0, statusText: 'Unknown Error' });
    expect(store.error()).toContain('Cannot reach the server');
  });
});
