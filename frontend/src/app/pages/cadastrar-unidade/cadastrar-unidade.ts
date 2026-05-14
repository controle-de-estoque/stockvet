import { Component } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { Navbar } from '../../components/navbar/navbar';
import { FormsModule } from '@angular/forms';
import { Api } from '../../api';

@Component({
  selector: 'app-cadastrar-unidade',
  imports: [ Navbar, RouterLink, FormsModule ],
  templateUrl: './cadastrar-unidade.html',
  styleUrl: './cadastrar-unidade.css',
})
export class CadastrarUnidade {
  nome: string = '';
  consumoMinimo: number | null = null;

  constructor(private api: Api, private router: Router) {}

  private extractErrorMessage(error: HttpErrorResponse): string {
    const body = error.error;

    if (typeof body === 'string') {
      try {
        const parsed = JSON.parse(body);
        return parsed?.message ?? body;
      } catch {
        return body;
      }
    }

    return body?.message ?? 'Falha ao criar unidade';
  }

  cadastrar() {
    const estoqueId = localStorage.getItem('estoque');
    if (!estoqueId) {
      window.alert('Estoque não identificado.');
      return;
    }

    if (!this.nome || !this.consumoMinimo) {
      window.alert('Preencha todos os campos.');
      return;
    }

    const payload = {
      nome: this.nome,
      consumoMinimo: this.consumoMinimo,
      estoque: estoqueId
    };

    this.api.cadastrarUnidade(payload).subscribe({
      next: () => {
        window.alert('Unidade cadastrada com sucesso!');
        this.nome = '';
        this.consumoMinimo = null;
        this.router.navigate(['/admin']);
      },
      error: (error: HttpErrorResponse) => {
        console.error(error);
        window.alert(this.extractErrorMessage(error));
      }
    });
  }
}
