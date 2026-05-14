import { Component } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { Navbar } from '../../components/navbar/navbar';
import { FormsModule } from '@angular/forms';
import { Api } from '../../api';

@Component({
  selector: 'app-cadastrar-usuario',
  imports: [Navbar, RouterLink, FormsModule],
  templateUrl: './cadastrar-usuario.html',
  styleUrl: './cadastrar-usuario.css',
})
export class CadastrarUsuario {

  firstName = '';
  lastName = '';
  email = '';
  password = '';
  firstPetName = '';

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

    return body?.message ?? 'Falha ao criar usuário';
  }

  cadastrarUsuario() {
    const estoqueId = localStorage.getItem('estoque');

    if (!estoqueId) {
      window.alert('Estoque não identificado.');
      return;
    }

    if (!this.firstName.trim() || !this.lastName.trim() || !this.email.trim() || !this.password || !this.firstPetName.trim()) {
      window.alert('Preencha todos os campos.');
      return;
    }

    this.api.cadastrarUsuario({
      firstName: this.firstName.trim(),
      lastName: this.lastName.trim(),
      email: this.email.trim(),
      password: this.password,
      firstPetName: this.firstPetName.trim(),
      estoque: estoqueId,
    }).subscribe({
      next: () => {
        window.alert('Usuário cadastrado com sucesso!');
        this.firstName = '';
        this.lastName = '';
        this.email = '';
        this.password = '';
        this.firstPetName = '';
        this.router.navigate(['/admin']);
      },
      error: (error: HttpErrorResponse) => {
        window.alert(this.extractErrorMessage(error));
      }
    });
  }

}
