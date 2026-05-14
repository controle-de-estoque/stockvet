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
  produtosPaginados = signal<Produto[]>([]);
  private searchSubject = new Subject<string>();
  private searchSubscription: Subscription;

  nomeProduto: string = '';
  somenteAtivos: boolean = true;
  paginaAtual: number = 1;
  tamanhoPagina: number = 10;
  totalPaginas: number = 1;

  constructor(private api: Api) {
    this.produtosFiltrados.set(this.produtos());
    this.produtosPaginados.set(this.produtos());

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
        this.produtos.set(produtos);
        this.executarFiltro(this.nomeProduto);
      },
      error: (err) => window.alert(err),
    });
  }

  onSearchChange(value: string) {
    this.searchSubject.next(value);
  }

  private executarFiltro(searchTerm: string, resetarPagina: boolean = true) {
    if (resetarPagina) {
      this.paginaAtual = 1;
    }

    let filtrados = this.produtos().filter((p: Produto) =>
      p.nome.toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (this.somenteAtivos) {
      filtrados = filtrados.filter((p) => p.ativo);
    }

    this.produtosFiltrados.set(filtrados);
    this.aplicarPaginacao(filtrados);
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

  irParaPagina(pagina: number) {
    if (pagina < 1 || pagina > this.totalPaginas) {
      return;
    }

    this.paginaAtual = pagina;
    this.aplicarPaginacao(this.produtosFiltrados());
  }

  paginaAnterior() {
    this.irParaPagina(this.paginaAtual - 1);
  }

  proximaPagina() {
    this.irParaPagina(this.paginaAtual + 1);
  }

  private aplicarPaginacao(produtos: Produto[]) {
    this.totalPaginas = Math.max(1, Math.ceil(produtos.length / this.tamanhoPagina));

    if (this.paginaAtual > this.totalPaginas) {
      this.paginaAtual = this.totalPaginas;
    }

    const inicio = (this.paginaAtual - 1) * this.tamanhoPagina;
    const fim = inicio + this.tamanhoPagina;
    this.produtosPaginados.set(produtos.slice(inicio, fim));
  }
}
