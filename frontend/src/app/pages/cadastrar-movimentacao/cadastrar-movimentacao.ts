import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Navbar } from '../../components/navbar/navbar';

type Step = 'info' | 'produtos' | 'lote';

type Produto = {
  id: string;
  nome: string;
  unidade: string;
};

type ItemMovimentacao = {
  id: number;
  produtoId: string;
  nome: string;
  quantidade: number;
  lote?: string;
};

@Component({
  selector: 'app-cadastrar-movimentacao',
  imports: [Navbar, RouterLink, FormsModule],
  templateUrl: './cadastrar-movimentacao.html',
  styleUrl: './cadastrar-movimentacao.css',
})
export class CadastrarMovimentacao {

  currentStep: Step = 'info';

  tipoMovimentacao = '';
  dataMovimentacao = '';
  horarioMovimentacao = '';
  cessionarioId = '';

  produtos: Produto[] = [
    { id: 'racao', nome: 'Racao Premium', unidade: 'kg' },
    { id: 'seringa', nome: 'Seringa 5ml', unidade: 'un' },
    { id: 'vacina', nome: 'Vacina V8', unidade: 'un' },
    { id: 'luva', nome: 'Luva Procedimento', unidade: 'cx' },
  ];

  produtoSelecionadoId = '';
  quantidadeSelecionada: number | null = null;

  itensMovimentacao: ItemMovimentacao[] = [];
  private nextItemId = 1;

  loteMarca = '';
  loteQuantidade: number | null = null;
  loteValidade = '';

  isStepInfo(): boolean {
    return this.currentStep === 'info';
  }

  isStepProdutos(): boolean {
    return this.currentStep !== 'info';
  }

  get produtoSelecionado(): Produto | undefined {
    return this.produtos.find((produto) => produto.id === this.produtoSelecionadoId);
  }

  podeIrParaProdutos(): boolean {
    return !!this.tipoMovimentacao && !!this.dataMovimentacao && !!this.horarioMovimentacao;
  }

  irParaProdutos(): void {
    if (this.podeIrParaProdutos()) {
      this.currentStep = 'produtos';
    }
  }

  voltarParaInfo(): void {
    this.currentStep = 'info';
  }

  voltarParaProdutos(): void {
    this.currentStep = 'produtos';
    this.limparLote();
  }

  onTipoChange(): void {
    this.itensMovimentacao = [];
    this.produtoSelecionadoId = '';
    this.quantidadeSelecionada = null;
    this.limparLote();
  }

  onProdutoSelecionado(): void {
    if (this.tipoMovimentacao === 'entrada' && this.produtoSelecionadoId) {
      this.prepararLote();
      this.currentStep = 'lote';
    }
  }

  podeAdicionarSaida(): boolean {
    return this.tipoMovimentacao === 'saida'
      && !!this.produtoSelecionadoId
      && (this.quantidadeSelecionada ?? 0) > 0;
  }

  adicionarSaida(): void {
    if (!this.podeAdicionarSaida()) {
      return;
    }

    const produto = this.produtoSelecionado;
    if (!produto) {
      return;
    }

    this.itensMovimentacao = [
      ...this.itensMovimentacao,
      {
        id: this.nextItemId++,
        produtoId: produto.id,
        nome: produto.nome,
        quantidade: Number(this.quantidadeSelecionada),
      },
    ];

    this.produtoSelecionadoId = '';
    this.quantidadeSelecionada = null;
  }

  podeAdicionarLote(): boolean {
    return this.tipoMovimentacao === 'entrada'
      && !!this.produtoSelecionadoId
      && !!this.loteMarca.trim()
      && (this.loteQuantidade ?? 0) > 0;
  }

  adicionarLote(): void {
    if (!this.podeAdicionarLote()) {
      return;
    }

    const produto = this.produtoSelecionado;
    if (!produto) {
      return;
    }

    let lote = this.loteMarca.trim();
    if (this.loteValidade) {
      lote = `${lote} | ${this.loteValidade}`;
    }

    this.itensMovimentacao = [
      ...this.itensMovimentacao,
      {
        id: this.nextItemId++,
        produtoId: produto.id,
        nome: produto.nome,
        quantidade: Number(this.loteQuantidade),
        lote,
      },
    ];

    this.produtoSelecionadoId = '';
    this.limparLote();
    this.currentStep = 'produtos';
  }

  removerItem(itemId: number): void {
    this.itensMovimentacao = this.itensMovimentacao.filter((item) => item.id !== itemId);
  }

  mostrarLoteColuna(): boolean {
    return this.tipoMovimentacao === 'entrada';
  }

  podeRegistrar(): boolean {
    return this.itensMovimentacao.length > 0;
  }

  private prepararLote(): void {
    this.loteMarca = '';
    this.loteQuantidade = null;
    this.loteValidade = '';
  }

  private limparLote(): void {
    this.loteMarca = '';
    this.loteQuantidade = null;
    this.loteValidade = '';
  }

}
