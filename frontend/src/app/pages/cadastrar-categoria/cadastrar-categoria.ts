import { Component } from '@angular/core';
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

  cadastrarCategoria() {
    console.log(this.nome);
    this.api.cadastrarCategoria({ nome: this.nome, estoque: localStorage.getItem('estoque')!}).subscribe({
      next: () => {
        window.alert("Categoria criada com sucesso");
      },
      error: () => {
        window.alert("Falha ao criar categoria");
      }
    })
  }
}
