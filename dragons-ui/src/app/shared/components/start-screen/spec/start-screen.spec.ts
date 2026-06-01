import { TestBed } from '@angular/core/testing';
import { StartScreen } from '../start-screen'

function create() {
  TestBed.configureTestingModule({ imports: [StartScreen] });
  TestBed.overrideComponent(StartScreen, { set: { template: '', imports: [] } });
  return TestBed.createComponent(StartScreen);
}

describe('StartScreen', () => {
  it('allows starting and emits null by default (allowNone=true)', () => {
    const cmp = create().componentInstance;
    expect(cmp.canStart()).toBe(true);

    let emitted: string | null | undefined = 'untouched';
    cmp.startGame.subscribe((v) => (emitted = v));
    cmp.emitStart();
    expect(emitted).toBeNull();
  });

  it('tracks the selected strategy', () => {
    const cmp = create().componentInstance;
    cmp.choose('low-risk');
    expect(cmp.selected()).toBe('low-risk');
    expect(cmp.isSelected('low-risk')).toBe(true);
    expect(cmp.isSelected('expected-value')).toBe(false);
  });

  it('blocks starting until a strategy is chosen when allowNone=false', () => {
    const fixture = create();
    fixture.componentRef.setInput('allowNone', false);
    const cmp = fixture.componentInstance;

    expect(cmp.canStart()).toBe(false);
    let emitted: string | null | undefined = 'untouched';
    cmp.startGame.subscribe((v) => (emitted = v));
    cmp.emitStart();
    expect(emitted).toBe('untouched');

    cmp.choose('expected-value');
    expect(cmp.canStart()).toBe(true);
    cmp.emitStart();
    expect(emitted).toBe('expected-value');
  });
});
