import { Component, signal, effect, OnDestroy } from '@angular/core';
import { Navbar } from '../../components/navbar/navbar';
import { Produto } from './Produto';
import { FormsModule } from "@angular/forms";
import { Subject, debounceTime, distinctUntilChanged, Subscription } from 'rxjs';

@Component({
  selector: 'app-products',
  imports: [Navbar, FormsModule],
  templateUrl: './products.html',
  styleUrl: './products.css',
})
export class Products implements OnDestroy {

  produtos = signal<Produto[]>([
    {
      nome: 'Vacina V10',
      categoria: 'Imunológicos',
      quantidade: 25,
      quantidadeCritica: 10,
      unidade: 'frascos'
    },
    {
      nome: 'Seringa 3ml com agulha',
      categoria: 'Insumos',
      quantidade: 150,
      quantidadeCritica: 50,
      unidade: 'unidades'
    },
    {
      nome: 'Dipirona Gotas 50mg/ml',
      categoria: 'Medicamentos',
      quantidade: 4, // Quantidade abaixo do nível crítico (para testar alertas)
      quantidadeCritica: 10,
      unidade: 'frascos'
    },
    {
      nome: 'Ração Gatos Castrados 10kg',
      categoria: 'Nutrição',
      quantidade: 12,
      quantidadeCritica: 5,
      unidade: 'pacotes'
    },
    {
      nome: 'Luvas de Procedimento P',
      categoria: 'Insumos',
      quantidade: 2, // Quantidade abaixo do nível crítico
      quantidadeCritica: 5,
      unidade: 'caixas'
    },
    {
      nome: 'Shampoo Clorexidina 2%',
      categoria: 'Higiene e Estética',
      quantidade: 18,
      quantidadeCritica: 8,
      unidade: 'frascos'
    },
    {
      nome: 'Fio de Sutura Nylon 3-0',
      categoria: 'Cirúrgico',
      quantidade: 30,
      quantidadeCritica: 15,
      unidade: 'envelopes'
    },
    {
      nome: 'Gaze Estéril (Pacote 10un)',
      categoria: 'Insumos',
      quantidade: 85,
      quantidadeCritica: 30,
      unidade: 'pacotes'
    }
  ]);
  produtosFiltrados = signal<Produto[]>([]);
  private searchSubject = new Subject<string>();
  private searchSubscription: Subscription;

  nomeProduto: string = '';

  constructor() {
    this.produtosFiltrados.set(this.produtos());

    this.searchSubscription = this.searchSubject.pipe(
      debounceTime(1000),
      distinctUntilChanged()
    ).subscribe(searchTerm => {
      this.executarFiltro(searchTerm);
    });
  }

  onSearchChange(value: string) {
    this.searchSubject.next(value);
  }

  private executarFiltro(searchTerm: string) {
    const filtrados = this.produtos().filter((p: Produto) =>
      p.nome.toLowerCase().includes(searchTerm.toLowerCase())
    );
    this.produtosFiltrados.set(filtrados);
  }

  ngOnDestroy() {
    this.searchSubscription.unsubscribe();
  }
}
