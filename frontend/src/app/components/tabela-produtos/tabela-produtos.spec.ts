import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TabelaProdutos } from './tabela-produtos';

describe('TabelaProdutos', () => {
  let component: TabelaProdutos;
  let fixture: ComponentFixture<TabelaProdutos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TabelaProdutos]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TabelaProdutos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
