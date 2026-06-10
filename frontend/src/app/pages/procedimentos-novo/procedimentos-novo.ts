import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { Navbar } from "../../components/navbar/navbar";
import { Api } from '../../api';

interface ProdutoSelecionado {
  nome: string;
  quantidade: number;
}

@Component({
  selector: 'app-procedimentos-novo',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, Navbar],
  templateUrl: './procedimentos-novo.html'
})
export class ProcedimentosNovo { 
  
  etapaAtual: number = 1;

  // Corrigido para alinhar com os nomes esperados no payload
  procedimento = {
    nomeProcedimento: '',
    nomeEspecie: '',
    genero: ''
  };

  novoProduto: ProdutoSelecionado = {
    nome: '',
    quantidade: 1
  };

  produtos: ProdutoSelecionado[] = [];

  constructor(private router: Router, private api: Api) {}

  avancarEtapa(): void {
    if (!this.procedimento.nomeProcedimento || !this.procedimento.nomeEspecie || !this.procedimento.genero) {
      alert('Por favor, preencha todos os dados iniciais antes de avançar.');
      return;
    }
    this.etapaAtual = 2;
  }

  voltarEtapa(): void {
    this.etapaAtual = 1;
  }

  adicionarProduto(): void {
    if (this.novoProduto.nome && this.novoProduto.quantidade > 0) {
      this.produtos.push({ ...this.novoProduto }); 
      this.novoProduto.nome = '';
      this.novoProduto.quantidade = 1;
    } else {
      alert('Informe o nome e uma quantidade válida.');
    }
  }

  removerProduto(index: number): void {
    this.produtos.splice(index, 1);
  }

  salvarProcedimento(): void {
    if (this.produtos.length === 0) {
      alert('Adicione pelo menos um produto.');
      return;
    }

    const estoqueId = localStorage.getItem('estoque') || '';

    const payload = {
      ...this.procedimento,
      estoque: estoqueId
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