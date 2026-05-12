import { Component, OnDestroy, signal } from '@angular/core';
import { Navbar } from '../../components/navbar/navbar';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Subject, Subscription, debounceTime, distinctUntilChanged } from 'rxjs';

type Movimentacao = {
  tipo: string;
  movimentadoPor: string;
  data: string;
  hora: string;
  cedidoA: string;
};

@Component({
  selector: 'app-movimentacoes',
  imports: [Navbar, FormsModule, RouterLink],
  templateUrl: './movimentacoes.html',
  styleUrl: './movimentacoes.css',
})
export class Movimentacoes implements OnDestroy {

  movimentacoes = signal<Movimentacao[]>([
    {
      tipo: 'Entrada',
      movimentadoPor: 'Joao Silva',
      data: '12/05/2026',
      hora: '09:30',
      cedidoA: 'Clinica Central',
    },
    {
      tipo: 'Saida',
      movimentadoPor: 'Marina Costa',
      data: '11/05/2026',
      hora: '16:10',
      cedidoA: 'Hospital Vet Mais',
    },
    {
      tipo: 'Transferencia',
      movimentadoPor: 'Rafael Lima',
      data: '10/05/2026',
      hora: '13:45',
      cedidoA: 'Filial Norte',
    },
  ]);
  movimentacoesFiltradas = signal<Movimentacao[]>([]);
  private searchSubject = new Subject<string>();
  private searchSubscription: Subscription;

  termoBusca = '';

  constructor() {
    this.movimentacoesFiltradas.set(this.movimentacoes());

    this.searchSubscription = this.searchSubject.pipe(
      debounceTime(600),
      distinctUntilChanged()
    ).subscribe(searchTerm => {
      this.executarFiltro(searchTerm);
    });
  }

  onSearchChange(value: string) {
    this.searchSubject.next(value);
  }

  private executarFiltro(searchTerm: string) {
    const termo = searchTerm.trim().toLowerCase();

    if (!termo) {
      this.movimentacoesFiltradas.set(this.movimentacoes());
      return;
    }

    const filtradas = this.movimentacoes().filter((m) =>
      m.tipo.toLowerCase().includes(termo) ||
      m.movimentadoPor.toLowerCase().includes(termo) ||
      m.cedidoA.toLowerCase().includes(termo)
    );

    this.movimentacoesFiltradas.set(filtradas);
  }

  ngOnDestroy() {
    this.searchSubscription.unsubscribe();
  }

}
