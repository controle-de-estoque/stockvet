import { Component, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Navbar } from '../../components/navbar/navbar';
import { Categoria, Unidade } from './Tipos';
import { Api } from '../../api';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-cadastrar-produto',
  imports: [Navbar, RouterLink, FormsModule],
  templateUrl: './cadastrar-produto.html',
  styleUrl: './cadastrar-produto.css',
})
export class CadastrarProduto implements OnInit{
  
  nome: string = '';
  categoriaId: string = '';
  unidadeId: string = '';
  tipo: string = '';
  quantidadeCritica: number | null = null;

  categorias = signal<Categoria[]>([]);
  unidades = signal<Unidade[]>([]);

  constructor(private api: Api, private router: Router) {

  }

  ngOnInit(): void {
    this.api.buscarCategoriasPorEstoque().subscribe({
      next: (response) => {
        this.categorias.set(response);
      },
      error: (err) => window.alert(err)
    });

    this.api.buscarUnidadesPorEstoque().subscribe({
      next: (response) => this.unidades.set(response),
      error: (err) => window.alert(err)
    });
  }

  cadastrarProduto() {
    const estoqueId = localStorage.getItem('estoque');
    if (!estoqueId) {
      window.alert('Estoque não identificado.');
      return;
    }

    if (!this.nome || !this.categoriaId || !this.unidadeId || !this.tipo || !this.quantidadeCritica) {
      window.alert('Preencha todos os campos obrigatórios.');
      return;
    }

    const payload = {
      nome: this.nome,
      categoria: this.categoriaId,
      unidade: this.unidadeId,
      tipo: this.tipo.toUpperCase(), // O backend espera o Enum (CONSUMO ou PATRIMONIO)
      estoque: estoqueId,
      quantidadeCritica: this.quantidadeCritica,
    };

    this.api.cadastrarProduto(payload).subscribe({
      next: (response) => {
        window.alert('Produto cadastrado com sucesso!');
        this.router.navigate(['/produtos']);
      },
      error: (err) => {
        console.error(err);
        window.alert('Erro ao cadastrar produto.');
      }
    });
  };
}
