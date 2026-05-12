import { Component, signal, OnDestroy, OnInit } from '@angular/core';
import { Navbar } from '../../components/navbar/navbar';
import { Produto } from './Produto';
import { FormsModule } from "@angular/forms";
import { Subject, debounceTime, distinctUntilChanged, Subscription } from 'rxjs';
import { Api } from '../../api';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-products',
  imports: [Navbar, FormsModule, RouterLink],
  templateUrl: './products.html',
  styleUrl: './products.css',
})
export class Products implements OnDestroy, OnInit {

  produtos = signal<Produto[]>([]);
  produtosFiltrados = signal<Produto[]>([]);
  private searchSubject = new Subject<string>();
  private searchSubscription: Subscription;

  nomeProduto: string = '';
  somenteAtivos: boolean = true;

  constructor(private api: Api) {
    this.produtosFiltrados.set(this.produtos());

    this.searchSubscription = this.searchSubject.pipe(
      debounceTime(1000),
      distinctUntilChanged()
    ).subscribe(searchTerm => {
      this.executarFiltro(searchTerm);
    });
  }
  ngOnInit(): void {
    this.api.buscarProdutos().subscribe({
      next: (produtos: Produto[]) => {
        const produtosComQuantidade = produtos.map((produto) => ({
          ...produto,
          quantidade: this.gerarQuantidadeMock(produto.nome),
        }));
        this.produtos.set(produtosComQuantidade);
        this.executarFiltro(this.nomeProduto);
      },
      error: (err) => window.alert(err),
    });
  }

  onSearchChange(value: string) {
    this.searchSubject.next(value);
  }

  private executarFiltro(searchTerm: string) {
    let filtrados = this.produtos().filter((p: Produto) =>
      p.nome.toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (this.somenteAtivos) {
      filtrados = filtrados.filter((p) => p.ativo);
    }

    this.produtosFiltrados.set(filtrados);
  }

  ngOnDestroy() {
    this.searchSubscription.unsubscribe();
  }

  onSomenteAtivosChange() {
    this.executarFiltro(this.nomeProduto);
  }

  confirmarDesativacao(produto: Produto) {
    if (!window.confirm('Gostaria de desativar este produto?')) {
      return;
    }

    this.api.desativarProduto(produto.id).subscribe({
      next: () => {
        const atualizados = this.produtos().map((item) =>
          item.id === produto.id ? { ...item, ativo: false } : item
        );
        this.produtos.set(atualizados);
        this.executarFiltro(this.nomeProduto);
      },
      error: (err) => window.alert(err),
    });
  }

  get totalProdutosAtivos(): number {
    return this.produtos().filter((p) => p.ativo).length;
  }

  get totalProdutosCadastrados(): number {
    return this.produtos().length;
  }

  get produtosEmAlerta(): number {
    return this.produtos().filter((p) => p.ativo && p.quantidade <= p.quantidadeCritica).length;
  }

  private gerarQuantidadeMock(nome: string): number {
    let soma = 0;
    for (let i = 0; i < nome.length; i += 1) {
      soma = (soma + nome.charCodeAt(i)) % 50;
    }
    return soma + 1;
  }
}
