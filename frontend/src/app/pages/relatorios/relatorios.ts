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
      window.alert('Por favor, selecione o tipo de relatório e o formato do arquivo.');
      return;
    }

    if (this.tipoRelatorio === 'saida') {

      if (this.formatoArquivo === 'xlsx') {
        this.api.baixarRelatorioEstoqueAtualExcel().subscribe({
          next: (blob: Blob) => this.efetuarDownloadNavegador(blob, 'relatorio-estoque-atual.xlsx'),
          error: (err) => {
            console.error('Erro ao baixar relatório XLSX', err);
            window.alert('Erro ao gerar o relatório XLSX no servidor.');
          }
        });
      } else if (this.formatoArquivo === 'pdf') {
        this.api.baixarRelatorioEstoqueAtualPdf().subscribe({
          next: (blob: Blob) => this.efetuarDownloadNavegador(blob, 'relatorio-estoque-atual.pdf'),
          error: (err) => {
            console.error('Erro ao baixar relatório PDF', err);
            window.alert('Erro ao gerar o relatório PDF no servidor.');
          }
        });
      }

    } else {
      window.alert('Funcionalidade ainda não implementada para este relatório/formato.');
    }
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
