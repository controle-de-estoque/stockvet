import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CadastrarUnidade } from './cadastrar-unidade';

describe('CadastrarUnidade', () => {
  let component: CadastrarUnidade;
  let fixture: ComponentFixture<CadastrarUnidade>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CadastrarUnidade]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CadastrarUnidade);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
