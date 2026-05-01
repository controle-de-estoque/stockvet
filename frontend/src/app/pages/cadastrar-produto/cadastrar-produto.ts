import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Navbar } from '../../components/navbar/navbar';

@Component({
  selector: 'app-cadastrar-produto',
  imports: [Navbar, RouterLink],
  templateUrl: './cadastrar-produto.html',
  styleUrl: './cadastrar-produto.css',
})
export class CadastrarProduto {

}
