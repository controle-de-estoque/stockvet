import { Component } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { Navbar } from '../../components/navbar/navbar';
import { Api } from '../../api';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-cadastrar-categoria',
  imports: [ Navbar, RouterLink, FormsModule ],
  templateUrl: './cadastrar-categoria.html',
  styleUrl: './cadastrar-categoria.css',
})
export class CadastrarCategoria {

  nome: string = '';

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

    return body?.message ?? 'Falha ao criar categoria';
  }

  cadastrarCategoria() {
    this.api.cadastrarCategoria({ nome: this.nome, estoque: localStorage.getItem('estoque')!}).subscribe({
      next: () => {
        window.alert("Categoria criada com sucesso");
        this.nome = '';
      },
      error: (error: HttpErrorResponse) => {
        if (error.status === 409) {
          window.alert(this.extractErrorMessage(error) ?? 'Já existe uma categoria com esse nome neste estoque');
          return;
        }

        window.alert(this.extractErrorMessage(error));
      }
    })
  }
}
