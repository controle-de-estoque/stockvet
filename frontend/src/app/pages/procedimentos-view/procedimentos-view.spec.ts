import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcedimentosView } from './procedimentos-view';

describe('ProcedimentosView', () => {
  let component: ProcedimentosView;
  let fixture: ComponentFixture<ProcedimentosView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProcedimentosView]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProcedimentosView);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
