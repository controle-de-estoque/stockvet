import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { Navbar } from "../../components/navbar/navbar";
import { Api } from '../../api';

interface ProdutoSelecionado {
  id: string;
  nome: string;
  quantidade: number;
  tipo: 'fixo' | 'variavel';
}

@Component({
  selector: 'app-procedimentos-novo',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, Navbar],
  templateUrl: './procedimentos-novo.html'
})
export class ProcedimentosNovo implements OnInit {
  etapaAtual: number = 1;
  produtosDisponiveis: any[] = [];
  
  procedimento = {
    nomeProcedimento: '',
    nomeEspecie: '',
    genero: ''
  };

  novoProduto = {
    id: '',
    nome: '',
    quantidade: 1,
    tipo: 'variavel' as 'fixo' | 'variavel'
  };

  produtos: ProdutoSelecionado[] = [];

  constructor(private router: Router, private api: Api) {}

  ngOnInit() {
    this.carregarProdutosEstoque();
  }

  carregarProdutosEstoque() {
    this.api.buscarProdutos().subscribe(data => {
      this.produtosDisponiveis = data.filter((p: any) => p.ativo);
    });
  }

  avancarEtapa(): void {
    if (!this.procedimento.nomeProcedimento || !this.procedimento.nomeEspecie || !this.procedimento.genero) {
      alert('Por favor, preencha todos os campos obrigatórios.');
      return;
    }
    this.etapaAtual = 2;
  }

  voltarEtapa(): void { this.etapaAtual = 1; }

  adicionarProduto(): void {
    const prod = this.produtosDisponiveis.find(p => p.id === this.novoProduto.id);
    if (prod && this.novoProduto.quantidade > 0) {
      this.produtos.push({
        id: prod.id,
        nome: prod.nome,
        quantidade: this.novoProduto.quantidade,
        tipo: this.novoProduto.tipo
      });
    } else {
      alert('Selecione um produto e uma quantidade válida.');
    }
  }

  removerProduto(index: number): void { this.produtos.splice(index, 1); }

  // NOVO MÉTODO ADICIONADO AQUI
  visualizarItem(id: string): void {
    // Atenção: este ID pertence ao produto da linha. 
    // Altere o caminho se a intenção for navegar para a tela do produto em si.
    this.router.navigate(['/procedimentos', id]);
  }

  salvarProcedimento(): void {
    if (this.produtos.length === 0) {
      alert('Adicione pelo menos um produto ao procedimento.');
      return;
    }

    const payload = {
      ...this.procedimento,
      estoque: localStorage.getItem('estoque') ?? '',
      itens: this.produtos.map(p => ({
        produtoId: p.id,
        quantidade: p.quantidade,
        tipo: p.tipo
      }))
    };

    this.api.cadastrarProcedimento(payload).subscribe({
      next: () => {
        alert('Procedimento cadastrado com sucesso!');
        this.router.navigate(['/procedimentos']);
      },
      error: (err) => {
        console.error(err);
        alert('Erro ao salvar procedimento.');
      }
    });
  }
}