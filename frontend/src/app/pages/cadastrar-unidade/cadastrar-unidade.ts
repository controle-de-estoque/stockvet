import { Component } from '@angular/core';
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
      },
      error: (err) => {
        console.error(err);
        window.alert('Erro ao cadastrar unidade.');
      }
    });
  }
}
