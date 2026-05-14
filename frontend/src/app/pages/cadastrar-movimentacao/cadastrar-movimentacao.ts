import { Component, OnInit, signal, computed, WritableSignal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Navbar } from '../../components/navbar/navbar';
import { Api } from '../../api';

type Step = 'info' | 'produtos' | 'lote';

type Produto = {
  id: string;
  nome: string;
  unidade: string;
  tipo: string;
};

type Cessionario = {
  id: string;
  nome: string;
  email: string;
};

type ItemMovimentacao = {
  id: number;
  produtoId: string;
  nome: string;
  quantidade: number;
  lote?: string;
  loteIdBackend?: string;
  dataValidadeBackend?: string;
};

@Component({
  selector: 'app-cadastrar-movimentacao',
  imports: [Navbar, RouterLink, FormsModule],
  templateUrl: './cadastrar-movimentacao.html',
  styleUrl: './cadastrar-movimentacao.css',
})
export class CadastrarMovimentacao implements OnInit {
  // --- Estados Base (Signals) ---
  currentStep = signal<Step>('info');

  tipoMovimentacao = signal('');
  dataMovimentacao = signal('');
  horarioMovimentacao = signal('');
  cessionarioId = signal('');

  cessionarios = signal<Cessionario[]>([]);
  produtos = signal<Produto[]>([]);

  produtoSelecionadoId = signal('');
  quantidadeSelecionada = signal<number | null>(null);

  itensMovimentacao = signal<ItemMovimentacao[]>([]);
  private nextItemId = 1;

  loteIdentificador = signal('');
  loteMarca = signal('');
  loteQuantidade = signal<number | null>(null);
  loteValidade = signal('');

  // --- Estados Derivados (Computed) ---
  isStepInfo = computed(() => this.currentStep() === 'info');

  isStepProdutos = computed(() => this.currentStep() !== 'info');

  produtoSelecionado = computed(() =>
    this.produtos().find((produto) => produto.id === this.produtoSelecionadoId())
  );

  podeIrParaProdutos = computed(() =>
    !!this.tipoMovimentacao() && !!this.dataMovimentacao() && !!this.horarioMovimentacao()
  );

  mostrarLoteColuna = computed(() => this.tipoMovimentacao() === 'entrada');

  podeRegistrar = computed(() => this.itensMovimentacao().length > 0);

  podeAdicionarSaida = computed(() => {
    return this.tipoMovimentacao() === 'saida'
      && !!this.produtoSelecionadoId()
      && (this.quantidadeSelecionada() ?? 0) > 0;
  });

  podeAdicionarLote = computed(() => {
    const produto = this.produtoSelecionado();
    if (!produto) return false;

    const isConsumo = produto.tipo?.toLowerCase() === 'consumo';
    const identificadorValido = isConsumo ? !!this.loteIdentificador().trim() : true;

    return this.tipoMovimentacao() === 'entrada'
      && identificadorValido
      && !!this.loteMarca().trim()
      && (this.loteQuantidade() ?? 0) > 0;
  });

  constructor(private api: Api) {}

  ngOnInit(): void {
    this.api.buscarCessionariosPorEstoque(localStorage.getItem("estoque")!).subscribe({
      next: (response) => this.cessionarios.set(response),
      error: (err) => window.alert("Falha ao carregar cessionários")
    });

    this.api.buscarProdutos().subscribe({
      next: (response) => this.produtos.set(response),
      error: (err) => window.alert("Falha ao carregar produtos")
    });
  }

  cadastrarMovimentacao() {
    if (this.itensMovimentacao().length === 0) {
      window.alert("Adicione pelo menos um item à movimentação.");
      return;
    }

    this.api.getIdFromUserEmail().subscribe({
      next: (userId) => {
        const cleanUserId = userId.replace(/"/g, '').trim();
        const estoqueId = localStorage.getItem("estoque")!;
        const dataHora = `${this.dataMovimentacao()}T${this.horarioMovimentacao()}`;

        const payloadLista = this.itensMovimentacao().map(item => ({
          produto: item.produtoId,
          estoque: estoqueId,
          movimentadoPor: cleanUserId,
          quantidade: item.quantidade.toString(),
          dataHoraMovimentacao: dataHora,
          tipo: this.tipoMovimentacao().toUpperCase(),
          loteId: item.loteIdBackend || "",
          dataValidade: item.dataValidadeBackend || ""
        }));

        if (this.tipoMovimentacao() === "entrada") {
          this.api.cadastrarMovimentacoesEntrada(payloadLista).subscribe({
            next: () => {
              window.alert("Entradas registradas com sucesso!");
              this.itensMovimentacao.set([]);
              this.voltarParaInfo();
            },
            error: (err) => {
              console.error("Erro na entrada", err);
              window.alert("Ocorreu um erro ao registrar as entradas.");
            }
          });
        } else if (this.tipoMovimentacao() === "saida") {
          this.api.cadastrarMovimentacoesSaida(payloadLista).subscribe({
            next: () => {
              window.alert("Saídas registradas com sucesso!");
              this.itensMovimentacao.set([]);
              this.voltarParaInfo();
            },
            error: (err) => {
              console.error("Erro na saída", err);
              window.alert("Ocorreu um erro ao registrar as saídas.");
            }
          });
        }

      },
      error: (err) => window.alert("Erro ao identificar o usuário. Verifique seu login.")
    });
  }

  irParaProdutos(): void {
    if (this.podeIrParaProdutos()) {
      this.currentStep.set('produtos');
    }
  }

  voltarParaInfo(): void {
    this.currentStep.set('info');
  }

  voltarParaProdutos(): void {
    this.currentStep.set('produtos');
    this.limparLote();
  }

  onTipoChange(): void {
    this.itensMovimentacao.set([]);
    this.produtoSelecionadoId.set('');
    this.quantidadeSelecionada.set(null);
    this.limparLote();
  }

  onProdutoSelecionado(): void {
    if (this.tipoMovimentacao() === 'entrada' && this.produtoSelecionadoId()) {
      this.prepararLote();
      this.currentStep.set('lote');
    }
  }

  adicionarSaida(): void {
    if (!this.podeAdicionarSaida()) {
      return;
    }

    const produto = this.produtoSelecionado();
    if (!produto) {
      return;
    }

    this.itensMovimentacao.update(itens => [
      ...itens,
      {
        id: this.nextItemId++,
        produtoId: produto.id,
        nome: produto.nome,
        quantidade: Number(this.quantidadeSelecionada()),
      },
    ]);

    this.produtoSelecionadoId.set('');
    this.quantidadeSelecionada.set(null);
  }

  adicionarLote(): void {
    if (!this.podeAdicionarLote()) {
      return;
    }

    const produto = this.produtoSelecionado();
    if (!produto) {
      return;
    }

    let loteDisplay = this.loteMarca().trim();
    if (this.loteIdentificador().trim()) {
      loteDisplay = `${this.loteIdentificador().trim()} - ${loteDisplay}`;
    }
    if (this.loteValidade()) {
      loteDisplay = `${loteDisplay} | Val: ${this.loteValidade()}`;
    }

    this.itensMovimentacao.update(itens => [
      ...itens,
      {
        id: this.nextItemId++,
        produtoId: produto.id,
        nome: produto.nome,
        quantidade: Number(this.loteQuantidade()),
        lote: loteDisplay,
        loteIdBackend: this.loteIdentificador().trim(),
        dataValidadeBackend: this.loteValidade()
      },
    ]);

    this.produtoSelecionadoId.set('');
    this.limparLote();
    this.currentStep.set('produtos');
  }

  removerItem(itemId: number): void {
    this.itensMovimentacao.update(itens => itens.filter((item) => item.id !== itemId));
  }

  private prepararLote(): void {
    this.loteIdentificador.set('');
    this.loteMarca.set('');
    this.loteQuantidade.set(null);
    this.loteValidade.set('');
  }

  private limparLote(): void {
    this.loteIdentificador.set('');
    this.loteMarca.set('');
    this.loteQuantidade.set(null);
    this.loteValidade.set('');
  }
}
