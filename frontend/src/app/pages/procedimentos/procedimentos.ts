import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { Navbar } from '../../components/navbar/navbar';
import { Api } from '../../api';

export interface Procedimento {
  id: string;
  nome: string;
  especie: string;
  genero: string;
  ativo: boolean;
}

@Component({
  selector: 'app-procedimentos',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, Navbar],
  templateUrl: './procedimentos.html'
})
export class ProcedimentosComponent implements OnInit {
  termoBusca = signal('');
  mostrarApenasAtivos = signal(true);
  procedimentos = signal<Procedimento[]>([]);

  totalRegistrados = computed(() => this.procedimentos().length);
  totalAtivos = computed(() => this.procedimentos().filter(p => p.ativo).length);
  totalInativos = computed(() => this.procedimentos().filter(p => !p.ativo).length);

  procedimentosFiltrados = computed(() => {
    let lista = this.procedimentos();
    if (this.mostrarApenasAtivos()) {
      lista = lista.filter(p => p.ativo);
    }
    const busca = this.termoBusca().toLowerCase().trim();
    if (!busca) return lista;
    return lista.filter(p => 
      p.nome.toLowerCase().includes(busca) ||
      p.especie.toLowerCase().includes(busca)
    );
  });

  constructor(private api: Api, private router: Router) {}

  ngOnInit(): void {
    this.carregarProcedimentos();
  }

  carregarProcedimentos(): void {
    this.api.buscarProcedimentos().subscribe({
      next: (data) => this.procedimentos.set(data),
      error: (err) => console.error(err)
    });
  }

  onSearchChange(valor: string): void {
    this.termoBusca.set(valor);
  }

  visualizarProcedimento(id: string): void {
    this.router.navigate(['/procedimentos', id]);
  }

  inativarProcedimento(id: string): void {
    if (confirm('Tem certeza que deseja inativar este procedimento?')) {
      this.api.inativarProcedimento(id).subscribe({
        next: () => {
          alert('Procedimento inativado com sucesso!');
          this.carregarProcedimentos();
        },
        error: (err) => alert('Erro ao inativar procedimento.')
      });
    }
  }
}