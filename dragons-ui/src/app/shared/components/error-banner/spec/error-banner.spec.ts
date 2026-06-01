import { TestBed } from '@angular/core/testing';
import { ErrorBanner } from '../error-banner';
import { GameStore } from '../../../../core/services/game-store';

describe('ErrorBanner', () => {
  it('dismiss clears the store error', () => {
    TestBed.configureTestingModule({ imports: [ErrorBanner], providers: [GameStore] });
    TestBed.overrideComponent(ErrorBanner, { set: { template: '', imports: [] } });
    const cmp = TestBed.createComponent(ErrorBanner).componentInstance;

    cmp.store.setError('boom');
    expect(cmp.store.error()).toBe('boom');
    cmp.dismiss();
    expect(cmp.store.error()).toBeNull();
  });
});
