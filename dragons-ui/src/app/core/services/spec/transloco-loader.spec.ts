import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { TranslocoHttpLoader } from '../transloco-loader';

describe('TranslocoHttpLoader', () => {
  let loader: TranslocoHttpLoader;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    loader = TestBed.inject(TranslocoHttpLoader);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('GETs the translation file for the requested language', () => {
    let result: unknown;
    loader.getTranslation('en').subscribe((t) => (result = t));
    const req = httpMock.expectOne('/assets/i18n/en.json');
    expect(req.request.method).toBe('GET');
    req.flush({ hello: 'world' });
    expect(result).toEqual({ hello: 'world' });
  });
});
