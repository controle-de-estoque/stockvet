import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Procedimentos } from './procedimentos';

describe('Procedimentos', () => {
  let component: Procedimentos;
  let fixture: ComponentFixture<Procedimentos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Procedimentos]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Procedimentos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
