import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Navbar } from '../../components/navbar/navbar';
import { Api } from '../../api';

@Component({
  selector: 'app-relatorios',
  standalone: true,
  imports: [Navbar, CommonModule, FormsModule],
  templateUrl: './relatorios.html',
  styleUrl: './relatorios.css',
})
export class Relatorios {
  tipoRelatorio: string = '';
  formatoArquivo: string = '';
  dataInicial: string = '';
  dataFinal: string = '';

  constructor(private api: Api) {}

  baixarRelatorio() {
    if (!this.tipoRelatorio || !this.formatoArquivo) {
      window.alert('Por favor, select o tipo de relatório e o formato do arquivo.');
      return;
    }

    const estoqueId = localStorage.getItem('estoque');
    if (!estoqueId) {
      window.alert('Identificador do estoque não encontrado no armazenamento local.');
      return;
    }

    // Monta o payload deixando as datas nulas caso não sejam preenchidas
    const payload = {
      estoque: estoqueId,
      inicio: this.dataInicial ? `${this.dataInicial}T00:00:00` : null,
      fim: this.dataFinal ? `${this.dataFinal}T23:59:59` : null
    };

    // 1. Consumo por período (value="entrada" no HTML) -> Endpoint: Historico Saídas
    if (this.tipoRelatorio === 'entrada') { 
      if (this.formatoArquivo === 'pdf') {
        this.api.baixarRelatorioSaidasPdf(payload).subscribe({
          next: (blob: Blob) => this.efetuarDownloadNavegador(blob, 'relatorio-consumo.pdf'),
          error: (err) => this.tratarErroDownload(err, 'PDF')
        });
      } else if (this.formatoArquivo === 'xlsx') {
        this.api.baixarRelatorioSaidasExcel(payload).subscribe({
          next: (blob: Blob) => this.efetuarDownloadNavegador(blob, 'relatorio-consumo.xlsx'),
          error: (err) => this.tratarErroDownload(err, 'XLSX')
        });
      }
    } 
    // 2. Estoque atual (value="saida" no HTML) -> Endpoint: Produtos Ativos
    else if (this.tipoRelatorio === 'saida') {
      if (this.formatoArquivo === 'xlsx') {
        this.api.baixarRelatorioProdutosAtivosExcel(payload).subscribe({
          next: (blob: Blob) => this.efetuarDownloadNavegador(blob, 'relatorio-estoque-atual.xlsx'),
          error: (err) => this.tratarErroDownload(err, 'XLSX')
        });
      } else if (this.formatoArquivo === 'pdf') {
        this.api.baixarRelatorioProdutosAtivosPdf(payload).subscribe({
          next: (blob: Blob) => this.efetuarDownloadNavegador(blob, 'relatorio-estoque-atual.pdf'),
          error: (err) => this.tratarErroDownload(err, 'PDF')
        });
      }
    } 
    // 3. Histórico de movimentações (value="geral" no HTML) -> Endpoint: Historico Movimentacoes
    else if (this.tipoRelatorio === 'geral') {
      if (this.formatoArquivo === 'pdf') {
        this.api.baixarRelatorioMovimentacoesPdf(payload).subscribe({
          next: (blob: Blob) => this.efetuarDownloadNavegador(blob, 'relatorio-movimentacoes.pdf'),
          error: (err) => this.tratarErroDownload(err, 'PDF')
        });
      } else if (this.formatoArquivo === 'xlsx') {
        this.api.baixarRelatorioMovimentacoesExcel(payload).subscribe({
          next: (blob: Blob) => this.efetuarDownloadNavegador(blob, 'relatorio-movimentacoes.xlsx'),
          error: (err) => this.tratarErroDownload(err, 'XLSX')
        });
      }
    } 
    // 4. Produtos próximos ao vencimento (value="vencimento" no HTML)
    else if (this.tipoRelatorio === 'vencimento') {
      window.alert('O relatório de produtos próximos ao vencimento ainda não foi implementado na API.');
    }
    else {
      window.alert('Tipo de relatório não reconhecido.');
    }
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