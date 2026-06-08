import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcedimentosNovo } from './procedimentos-novo';

describe('ProcedimentosNovo', () => {
  let component: ProcedimentosNovo;
  let fixture: ComponentFixture<ProcedimentosNovo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProcedimentosNovo]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProcedimentosNovo);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
