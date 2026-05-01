import { Component, signal, effect, OnDestroy, OnInit } from '@angular/core';
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
    this.api.buscarProdutos().subscribe(p => this.produtos.set(p));
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
