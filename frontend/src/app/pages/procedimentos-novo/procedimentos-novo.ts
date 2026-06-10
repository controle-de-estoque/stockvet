import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { Navbar } from "../../components/navbar/navbar";

// Interface para tipar os produtos
interface ProdutoSelecionado {
  nome: string;
  quantidade: number;
}

@Component({
  selector: 'app-procedimentos-novo',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, Navbar], // O FormsModule aqui corrige o erro do ngModel
  templateUrl: './procedimentos-novo.html'
})
export class ProcedimentosNovo { 
  
  // As propriedades precisam estar EXATAMENTE aqui, dentro desta classe
  etapaAtual: number = 1;

  procedimento = {
    nome: '',
    especie: '',
    genero: ''
  };

  novoProduto: ProdutoSelecionado = {
    nome: '',
    quantidade: 1
  };

  produtos: ProdutoSelecionado[] = [];

  constructor(private router: Router) {}

  avancarEtapa(): void {
    if (!this.procedimento.nome || !this.procedimento.especie || !this.procedimento.genero) {
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
      alert('Informe o nome e uma quantidade válida para o produto.');
    }
  }

  removerProduto(index: number): void {
    this.produtos.splice(index, 1);
  }

  salvarProcedimento(): void {
    if (this.produtos.length === 0) {
      alert('Adicione pelo menos um produto para referência de 1kg.');
      return;
    }

    const payload = {
      ...this.procedimento,
      produtos: this.produtos
    };

    console.log('Dados prontos para envio para a API:', payload);
    alert('Procedimento cadastrado com sucesso!');
    
    this.router.navigate(['/procedimentos']);
  }
}