import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Navbar } from '../../components/navbar/navbar';

export interface Procedimento {
  id: number;
  nome: string;
  especie: string;
  genero: 'Macho' | 'Fêmea' | 'Ambos';
  peso: number;
  produtos: string;
}

@Component({
  selector: 'app-procedimentos',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    RouterModule,
    Navbar
  ],
  templateUrl: './procedimentos.html',
  styleUrl: './procedimentos.css'
})
export class ProcedimentosComponent {
  
  // Variável ligada ao input de busca via [(ngModel)]
  termoBusca: string = '';

  // 2. Mock de dados (Simulando o que viria do seu Backend/API)
  procedimentos: Procedimento[] = [
    {
      id: 1,
      nome: 'Castração',
      especie: 'Canina',
      genero: 'Macho',
      peso: 15.0,
      produtos: 'Anestésico (10ml), Fio de Sutura (1 un), Gaze (5 un)'
    },
    {
      id: 2,
      nome: 'Limpeza de Tártaro',
      especie: 'Felina',
      genero: 'Fêmea',
      peso: 4.2,
      produtos: 'Sedativo (2ml), Pasta Profilática (1 un), Soro (50ml)'
    },
    {
      id: 3,
      nome: 'Amputação de Membro',
      especie: 'Canina',
      genero: 'Ambos',
      peso: 22.5,
      produtos: 'Anestésico (20ml), Fio Cirúrgico (3 un), Antibiótico (1 un)'
    },
    {
      id: 4,
      nome: 'Castração',
      especie: 'Felina',
      genero: 'Fêmea',
      peso: 3.5,
      produtos: 'Anestésico (5ml), Fio de Sutura (1 un), Escalpelo (1 un)'
    }
  ];

  // 3. Método acionado ao digitar no input (ngModelChange)
  onSearchChange(termo: string): void {
    this.termoBusca = termo;
  }

  procedimentosFiltrados(): Procedimento[] {
    if (!this.termoBusca) {
      return this.procedimentos;
    }

    const busca = this.termoBusca.toLowerCase().trim();

    return this.procedimentos.filter(p => 
      p.nome.toLowerCase().includes(busca) ||
      p.especie.toLowerCase().includes(busca)
    );
  }

  deletarProcedimento(id: number): void {
    if(confirm('Tem certeza que deseja deletar este procedimento?')) {
      // Remove o procedimento da lista localmente (mock)
      this.procedimentos = this.procedimentos.filter(p => p.id !== id);
    }
  }
}