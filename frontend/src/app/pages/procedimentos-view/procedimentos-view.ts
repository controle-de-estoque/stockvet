import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Navbar } from '../../components/navbar/navbar';
import { Api } from '../../api';

export interface ItemProcedimentoDetalhe {
  id: string;
  produtoId: string;
  nome: string;
  quantidade: number;
  tipo: 'fixo' | 'variavel';
}

export interface ProcedimentoDetalhe {
  id: string;
  nome: string;
  especie: string;
  genero: string;
  ativo: boolean;
  itens: ItemProcedimentoDetalhe[];
}

@Component({
  selector: 'app-procedimentos-view',
  standalone: true,
  imports: [CommonModule, Navbar, RouterLink],
  templateUrl: './procedimentos-view.html'
})
export class ProcedimentosViewComponent implements OnInit {
  procedimento = signal<ProcedimentoDetalhe | null>(null);
  carregando = signal<boolean>(true);

  private route = inject(ActivatedRoute);
  private api = inject(Api);

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.carregarProcedimento(id);
    }
  }

  private carregarProcedimento(id: string): void {
    this.carregando.set(true);
    this.api.buscarProcedimentoPorId(id).subscribe({
      next: (data) => {
        this.procedimento.set(data);
        this.carregando.set(false);
      },
      error: (err) => {
        console.error('Erro ao carregar os detalhes do procedimento:', err);
        this.carregando.set(false);
      }
    });
  }

  formatarGenero(genero?: string): string {
    if (!genero) return '-';
    const g = genero.toUpperCase();
    if (g === 'M' || g === 'MASCULINO') return 'Masculino';
    if (g === 'F' || g === 'FEMININO') return 'Feminino';
    return genero;
  }
}