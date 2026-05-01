import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Navbar } from '../../components/navbar/navbar';

@Component({
  selector: 'app-cadastrar-categoria',
  imports: [ Navbar, RouterLink ],
  templateUrl: './cadastrar-categoria.html',
  styleUrl: './cadastrar-categoria.css',
})
export class CadastrarCategoria {

}
