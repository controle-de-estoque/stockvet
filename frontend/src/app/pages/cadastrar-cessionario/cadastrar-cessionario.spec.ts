import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CadastrarCessionario } from './cadastrar-cessionario';

describe('CadastrarCessionario', () => {
  let component: CadastrarCessionario;
  let fixture: ComponentFixture<CadastrarCessionario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CadastrarCessionario]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CadastrarCessionario);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
