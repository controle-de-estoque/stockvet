import { Component, OnDestroy, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Navbar } from '../../components/navbar/navbar';
import { Api } from '../../api';
import { Subscription, catchError, finalize, of, timeout } from 'rxjs';
import ApexCharts from 'apexcharts';

type RelatorioFiltro = {
  tipo: string;
  inicio: string | null;
  fim: string | null;
};

type MovimentacaoApi = {
  id?: string;
  tipo: string;
  quantidade?: number | string;
  dataHoraMovimentacao: string;
};

@Component({
  selector: 'app-relatorios',
  standalone: true,
  imports: [Navbar, CommonModule, FormsModule],
  templateUrl: './relatorios.html',
  styleUrl: './relatorios.css',
})
export class Relatorios implements OnDestroy, AfterViewInit {
  @ViewChild('entradaChartDiv', { static: false }) entradaChartDiv?: ElementRef<HTMLDivElement>;
  @ViewChild('saidaChartDiv', { static: false }) saidaChartDiv?: ElementRef<HTMLDivElement>;

  tipoRelatorio: string = '';
  formatoArquivo: string = '';
  dataInicial: string = '';
  dataFinal: string = '';

  loading = false;
  chartVisible = false;
  errorMessage = '';

  private entradaChart?: ApexCharts;
  private saidaChart?: ApexCharts;
  private apiSubscription?: Subscription;

  constructor(private api: Api) {}

  ngAfterViewInit(): void {
    const today = new Date();
    this.dataFinal = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;

    this.initializeCharts().then(() => {
      setTimeout(() => {
        this.onRelatorioFiltersChange();
      }, 500);
    });
  }

  ngOnDestroy(): void {
    this.apiSubscription?.unsubscribe();
  }

  private initializeCharts(): Promise<void> {
    this.entradaChart = undefined;
    this.saidaChart = undefined;
    return Promise.resolve();
  }

  onRelatorioFiltersChange(event?: Event): void {
    if (event?.target) {
      const input = event.target as HTMLInputElement;
      if (input.id === 'data_inicial') this.dataInicial = input.value;
      if (input.id === 'data_final') this.dataFinal = input.value;
    }

    const today = new Date();
    const todayFormatted = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;

    const filtro: RelatorioFiltro = {
      tipo: this.tipoRelatorio,
      inicio: this.dataInicial ? `${this.dataInicial}T00:00:00` : null,
      fim: this.dataFinal ? `${this.dataFinal}T23:59:59` : `${todayFormatted}T23:59:59`,
    };

    this.fetchCharts(filtro);
  }

  baixarRelatorio() {
    if (!this.tipoRelatorio || !this.formatoArquivo) {
      window.alert('Por favor, selecione o tipo de relatório e o formato do arquivo.');
      return;
    }

    const estoqueId = localStorage.getItem('estoque');
    if (!estoqueId) {
      window.alert('Identificador do estoque não encontrado no armazenamento local.');
      return;
    }

    const payload = {
      estoque: estoqueId,
      inicio: this.dataInicial ? `${this.dataInicial}T00:00:00` : null,
      fim: this.dataFinal ? `${this.dataFinal}T23:59:59` : null,
    };

    if (this.tipoRelatorio === 'entrada') {
      if (this.formatoArquivo === 'pdf') {
        this.api.baixarRelatorioSaidasPdf(payload).subscribe({
          next: (blob: Blob) => this.efetuarDownloadNavegador(blob, 'relatorio-consumo.pdf'),
          error: (err) => this.tratarErroDownload(err, 'PDF'),
        });
      } else if (this.formatoArquivo === 'xlsx') {
        this.api.baixarRelatorioSaidasExcel(payload).subscribe({
          next: (blob: Blob) => this.efetuarDownloadNavegador(blob, 'relatorio-consumo.xlsx'),
          error: (err) => this.tratarErroDownload(err, 'XLSX'),
        });
      }
    } else if (this.tipoRelatorio === 'saida') {
      if (this.formatoArquivo === 'xlsx') {
        this.api.baixarRelatorioProdutosAtivosExcel(payload).subscribe({
          next: (blob: Blob) => this.efetuarDownloadNavegador(blob, 'relatorio-estoque-atual.xlsx'),
          error: (err) => this.tratarErroDownload(err, 'XLSX'),
        });
      } else if (this.formatoArquivo === 'pdf') {
        this.api.baixarRelatorioProdutosAtivosPdf(payload).subscribe({
          next: (blob: Blob) => this.efetuarDownloadNavegador(blob, 'relatorio-estoque-atual.pdf'),
          error: (err) => this.tratarErroDownload(err, 'PDF'),
        });
      }
    } else if (this.tipoRelatorio === 'geral') {
      if (this.formatoArquivo === 'pdf') {
        this.api.baixarRelatorioMovimentacoesPdf(payload).subscribe({
          next: (blob: Blob) => this.efetuarDownloadNavegador(blob, 'relatorio-movimentacoes.pdf'),
          error: (err) => this.tratarErroDownload(err, 'PDF'),
        });
      } else if (this.formatoArquivo === 'xlsx') {
        this.api.baixarRelatorioMovimentacoesExcel(payload).subscribe({
          next: (blob: Blob) => this.efetuarDownloadNavegador(blob, 'relatorio-movimentacoes.xlsx'),
          error: (err) => this.tratarErroDownload(err, 'XLSX'),
        });
      }
    } else if (this.tipoRelatorio === 'vencimento') {
      if (this.formatoArquivo === 'pdf') {
        this.api.baixarRelatorioVencimentoPdf(payload).subscribe({
          next: (blob: Blob) => this.efetuarDownloadNavegador(blob, 'relatorio-vencimento.pdf'),
          error: (err) => this.tratarErroDownload(err, 'PDF'),
        });
      } else if (this.formatoArquivo === 'xlsx') {
        this.api.baixarRelatorioVencimentoExcel(payload).subscribe({
          next: (blob: Blob) => this.efetuarDownloadNavegador(blob, 'relatorio-vencimento.xlsx'),
          error: (err) => this.tratarErroDownload(err, 'XLSX'),
        });
      }
    } else {
      window.alert('Tipo de relatório não reconhecido.');
    }
  }

  private fetchCharts(filtro: RelatorioFiltro): void {
    this.chartVisible = true;
    const estoqueId = localStorage.getItem('estoque');
    if (!estoqueId) {
      this.loading = false;
      this.errorMessage = 'Identificador do estoque não encontrado no armazenamento local.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.apiSubscription?.unsubscribe();
    this.apiSubscription = this.api.buscarMovimentacoes()
      .pipe(
        timeout(15000),
        catchError((error) => {
          console.error('Erro ao buscar movimentações para gráfico', error);
          if (error?.name === 'TimeoutError') {
            this.errorMessage = 'A requisição de movimentações levou tempo demais. Tente novamente mais tarde.';
          } else {
            this.errorMessage = 'Falha ao carregar os dados do gráfico.';
          }
          return of(null);
        }),
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe((movimentacoes: MovimentacaoApi[] | null) => {
        console.log('Movimentações recebidas:', movimentacoes);
        if (movimentacoes && movimentacoes.length > 0) {
          console.log('Primeiro objeto completo:', JSON.stringify(movimentacoes[0], null, 2));
          console.log('Chaves do primeiro objeto:', Object.keys(movimentacoes[0]));
        }
        if (!movimentacoes) return;
        this.buildCharts(movimentacoes, filtro);
      });
  }

  private shouldFetchCharts(): boolean {
    return true;
  }

  private resetCharts(): void {}

  private buildCharts(movimentacoes: MovimentacaoApi[], filtro: RelatorioFiltro): void {
    const movimentosFiltrados = movimentacoes
      .map((mov) => ({ ...mov, tipo: String(mov.tipo || '').toLowerCase() }))
      .filter((mov) => this.isInDateRange(mov.dataHoraMovimentacao, filtro.inicio, filtro.fim));

    const entradas = movimentosFiltrados.filter((mov) => mov.tipo === 'entrada');
    const saidas = movimentosFiltrados.filter((mov) => mov.tipo === 'saida');

    const entradasPorMes = this.aggregatePorMes(entradas);
    const saidasPorMes = this.aggregatePorMes(saidas);

    const allKeys = this.generateMonthRange(filtro.inicio, filtro.fim);
    const labels = allKeys.map((key) => this.formatMonthLabelFromKey(key));
    const entradaData = allKeys.map((key) => entradasPorMes.get(key)?.value ?? 0);
    const saidaData = allKeys.map((key) => saidasPorMes.get(key)?.value ?? 0);

    const baseOptions = {
      colors: ['#4e43e7'],
      grid: { borderColor: 'rgba(226, 232, 240, 0.7)', padding: { left: 10, right: 10, bottom: 20 } },
      xaxis: { categories: labels, type: 'category' as const, labels: { style: { colors: '#374151', fontSize: '12px' } } },
      yaxis: { min: 0, forceNiceScale: true, labels: { style: { colors: '#374151' } } },
      dataLabels: { enabled: false },
      tooltip: { x: { show: true } },
      chart: { toolbar: { show: false }, height: 280 },
    };

    // Destroy and recreate entrada chart
    if (this.entradaChart) { this.entradaChart.destroy(); }
    if (this.entradaChartDiv?.nativeElement) {
      this.entradaChart = new ApexCharts(this.entradaChartDiv.nativeElement, {
        ...baseOptions,
        chart: { ...baseOptions.chart, type: 'line' as const },
        series: [{ name: 'Entradas', data: entradaData }],
        stroke: { curve: 'smooth', width: 3 },
        markers: { size: 6, strokeWidth: 2, strokeColors: ['#4e43e7'] },
        fill: { type: 'solid', opacity: 0.15 },
      });
      this.entradaChart.render();
    }

    // Destroy and recreate saida chart
    if (this.saidaChart) { this.saidaChart.destroy(); }
    if (this.saidaChartDiv?.nativeElement) {
      this.saidaChart = new ApexCharts(this.saidaChartDiv.nativeElement, {
        ...baseOptions,
        chart: { ...baseOptions.chart, type: 'bar' as const },
        series: [{ name: 'Saídas', data: saidaData }],
        plotOptions: { bar: { borderRadius: 4 } },
      });
      this.saidaChart.render();
    }

    this.chartVisible = true;
    this.errorMessage = '';

    console.log('Relatorios: allKeys', allKeys);
    console.log('Relatorios: labels', labels);
    console.log('Relatorios: entradaData', entradaData);
    console.log('Relatorios: saidaData', saidaData);
  }

  private isInDateRange(dateString: string, inicio: string | null, fim: string | null): boolean {
    const data = new Date(dateString);
    if (Number.isNaN(data.getTime())) {
      return false;
    }

    if (inicio) {
      const inicioData = new Date(inicio);
      if (data < inicioData) {
        return false;
      }
    }

    if (fim) {
      const fimData = new Date(fim);
      if (data > fimData) {
        return false;
      }
    }

    return true;
  }

  private aggregatePorMes(movimentacoes: MovimentacaoApi[]): Map<string, { label: string; value: number }> {
    const agrupamento = new Map<string, { label: string; value: number }>();

    movimentacoes.forEach((mov) => {
      const data = new Date(mov.dataHoraMovimentacao);
      if (Number.isNaN(data.getTime())) {
        return;
      }

      const key = `${data.getFullYear()}-${String(data.getMonth() + 1).padStart(2, '0')}`;
      const label = this.formatMonthLabel(data);
      const current = agrupamento.get(key);

      if (current) {
        current.value += 1;
      } else {
        agrupamento.set(key, { label, value: 1 });
      }
    });

    return agrupamento;
  }

  private generateMonthRange(inicio: string | null, fim: string | null): string[] {
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);

    let dataFim = fim ? new Date(fim) : new Date(hoje);
    dataFim.setHours(0, 0, 0, 0);
    dataFim.setDate(1);

    // Default start: 6 months back from dataFim
    const defaultInicio = new Date(dataFim);
    defaultInicio.setMonth(defaultInicio.getMonth() - 5);

    let inicioEfetivo: Date;
    if (inicio) {
      const dataInicio = new Date(inicio);
      dataInicio.setHours(0, 0, 0, 0);
      dataInicio.setDate(1);
      // dataInicial only restricts (moves start forward), never expands beyond 6 months
      inicioEfetivo = dataInicio > defaultInicio ? dataInicio : defaultInicio;
    } else {
      inicioEfetivo = defaultInicio;
    }

    const result: string[] = [];
    const current = new Date(inicioEfetivo);

    while (current <= dataFim) {
      const key = `${current.getFullYear()}-${String(current.getMonth() + 1).padStart(2, '0')}`;
      result.push(key);
      current.setMonth(current.getMonth() + 1);
    }

    // Hard cap: nunca exibe mais que 6 meses, sempre mantém os mais recentes
    if (result.length > 6) {
      result.splice(0, result.length - 6);
    }

    return result.sort();
  }

  private buildOrderedMonthKeys(...groups: Map<string, { label: string; value: number }>[]): string[] {
    return Array.from(
      groups.reduce((set, group) => {
        group.forEach((_, key) => set.add(key));
        return set;
      }, new Set<string>())
    ).sort();
  }

  private formatMonthLabel(date: Date): string {
    return date.toLocaleString('pt-BR', { month: 'short', year: 'numeric' });
  }

  private formatMonthLabelFromKey(key: string): string {
    const [year, month] = key.split('-').map(Number);
    const date = new Date(year, month - 1, 1);
    const mes = date.toLocaleString('pt-BR', { month: 'short' }).replace('.', '');
    return `${mes} ${year}`;
}

  private tratarErroDownload(err: any, formato: string) {
    console.error(`Erro ao baixar relatório ${formato}`, err);
    window.alert(`Erro ao gerar o relatório ${formato} no servidor.`);
  }

  private efetuarDownloadNavegador(blob: Blob, nomeArquivo: string) {
    const url = window.URL.createObjectURL(blob);
    const ancora = document.createElement('a');

    ancora.href = url;
    ancora.download = nomeArquivo;

    document.body.appendChild(ancora);
    ancora.click();
    document.body.removeChild(ancora);

    window.URL.revokeObjectURL(url);
  }
}
