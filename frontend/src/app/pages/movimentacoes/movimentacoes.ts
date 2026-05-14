import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { Navbar } from '../../components/navbar/navbar';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Subject, Subscription, debounceTime, distinctUntilChanged } from 'rxjs';
import { Api } from '../../api';

type Movimentacao = {
  tipo: string;
  movimentadoPor: string;
  data: string;
  hora: string;
  cedidoA: string;
};

type MovimentacaoApi = {
  id: string;
  tipo: string;
  movimentadoPor: string;
  cedidoA: string | null;
  dataHoraMovimentacao: string;
};

@Component({
  selector: 'app-movimentacoes',
  imports: [Navbar, FormsModule, RouterLink],
  templateUrl: './movimentacoes.html',
  styleUrl: './movimentacoes.css',
})
export class Movimentacoes implements OnInit, OnDestroy {

  movimentacoes = signal<Movimentacao[]>([]);
  movimentacoesFiltradas = signal<Movimentacao[]>([]);
  private searchSubject = new Subject<string>();
  private searchSubscription: Subscription;

  termoBusca = '';

  constructor(private api: Api) {
    this.movimentacoesFiltradas.set(this.movimentacoes());

    this.searchSubscription = this.searchSubject.pipe(
      debounceTime(600),
      distinctUntilChanged()
    ).subscribe(searchTerm => {
      this.executarFiltro(searchTerm);
    });
  }

  ngOnInit(): void {
    const estoqueId = localStorage.getItem('estoque');
    if (!estoqueId) {
      window.alert('Estoque não identificado.');
      return;
    }

    this.api.buscarMovimentacoes().subscribe({
      next: (movimentacoes: MovimentacaoApi[]) => {
        const formatadas = movimentacoes.map((mov) => this.formatarMovimentacao(mov));
        this.movimentacoes.set(formatadas);
        this.executarFiltro(this.termoBusca);
      },
      error: (err) => window.alert(err),
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

  private formatarMovimentacao(mov: MovimentacaoApi): Movimentacao {
    const dataHora = new Date(mov.dataHoraMovimentacao);
    const data = Number.isNaN(dataHora.getTime()) ? '-' : dataHora.toLocaleDateString('pt-BR');
    const hora = Number.isNaN(dataHora.getTime()) ? '-' : dataHora.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
    const tipo = this.formatarTipo(mov.tipo);

    return {
      tipo,
      movimentadoPor: mov.movimentadoPor,
      data,
      hora,
      cedidoA: mov.cedidoA ?? '-',
    };
  }

  private formatarTipo(tipo: string): string {
    const normalizado = tipo.toLowerCase();
    if (normalizado === 'entrada') {
      return 'Entrada';
    }
    if (normalizado === 'saida') {
      return 'Saida';
    }
    return tipo;
  }

  ngOnDestroy() {
    this.searchSubscription.unsubscribe();
  }

}
