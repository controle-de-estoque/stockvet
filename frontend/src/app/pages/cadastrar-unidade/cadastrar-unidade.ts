import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Navbar } from '../../components/navbar/navbar';

@Component({
  selector: 'app-cadastrar-unidade',
  imports: [ Navbar, RouterLink ],
  templateUrl: './cadastrar-unidade.html',
  styleUrl: './cadastrar-unidade.css',
})
export class CadastrarUnidade {

}
