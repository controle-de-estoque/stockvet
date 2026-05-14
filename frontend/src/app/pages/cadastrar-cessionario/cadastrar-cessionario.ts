import { Component } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { Navbar } from '../../components/navbar/navbar';
import { FormsModule } from '@angular/forms';
import { Api } from '../../api';

@Component({
  selector: 'app-cadastrar-cessionario',
  imports: [Navbar, RouterLink, FormsModule],
  templateUrl: './cadastrar-cessionario.html',
  styleUrl: './cadastrar-cessionario.css',
})
export class CadastrarCessionario {

  nome: string = '';
  email: string = '';

  constructor(private api: Api) {}

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

    return body?.message ?? 'Falha ao criar cessionário';
  }

  cadastrarCessionario() {
    const estoqueId = localStorage.getItem('estoque');

    if (!estoqueId) {
      window.alert('Estoque não identificado.');
      return;
    }

    if (!this.nome.trim() || !this.email.trim()) {
      window.alert('Preencha todos os campos.');
      return;
    }

    this.api.cadastrarCessionario({
      nome: this.nome.trim(),
      email: this.email.trim(),
      estoque: estoqueId,
    }).subscribe({
      next: () => {
        window.alert('Cessionário cadastrado com sucesso!');
        this.nome = '';
        this.email = '';
      },
      error: (error: HttpErrorResponse) => {
        window.alert(this.extractErrorMessage(error));
      }
    });
  }

}
