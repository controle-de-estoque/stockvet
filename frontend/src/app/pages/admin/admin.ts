import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Navbar } from '../../components/navbar/navbar';

type AdminMovimentacao = {
  tipo: string;
  movimentadoPor: string;
  data: string;
  hora: string;
  cedidoA: string;
};

@Component({
  selector: 'app-admin',
  imports: [Navbar, RouterLink],
  templateUrl: './admin.html',
  styleUrl: './admin.css',
})
export class Admin {

  usuarios: AdminMovimentacao[] = [
    {
      tipo: 'Entrada',
      movimentadoPor: 'Joao Pedro',
      data: '12/03/2026',
      hora: '14:43',
      cedidoA: 'Pedro Henrique',
    },
    {
      tipo: 'Entrada',
      movimentadoPor: 'Joao Pedro',
      data: '12/03/2026',
      hora: '14:43',
      cedidoA: 'Pedro Henrique',
    },
    {
      tipo: 'Entrada',
      movimentadoPor: 'Joao Pedro',
      data: '12/03/2026',
      hora: '14:43',
      cedidoA: 'Pedro Henrique',
    },
    {
      tipo: 'Entrada',
      movimentadoPor: 'Joao Pedro',
      data: '12/03/2026',
      hora: '14:43',
      cedidoA: 'Pedro Henrique',
    },
  ];

  cessionarios: AdminMovimentacao[] = [
    {
      tipo: 'Entrada',
      movimentadoPor: 'Joao Pedro',
      data: '12/03/2026',
      hora: '14:43',
      cedidoA: 'Pedro Henrique',
    },
    {
      tipo: 'Entrada',
      movimentadoPor: 'Joao Pedro',
      data: '12/03/2026',
      hora: '14:43',
      cedidoA: 'Pedro Henrique',
    },
    {
      tipo: 'Entrada',
      movimentadoPor: 'Joao Pedro',
      data: '12/03/2026',
      hora: '14:43',
      cedidoA: 'Pedro Henrique',
    },
    {
      tipo: 'Entrada',
      movimentadoPor: 'Joao Pedro',
      data: '12/03/2026',
      hora: '14:43',
      cedidoA: 'Pedro Henrique',
    },
  ];

}
