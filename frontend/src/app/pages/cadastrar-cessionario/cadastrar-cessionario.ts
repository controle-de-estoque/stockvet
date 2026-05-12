import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Navbar } from '../../components/navbar/navbar';

@Component({
  selector: 'app-cadastrar-cessionario',
  imports: [Navbar, RouterLink],
  templateUrl: './cadastrar-cessionario.html',
  styleUrl: './cadastrar-cessionario.css',
})
export class CadastrarCessionario {

}
