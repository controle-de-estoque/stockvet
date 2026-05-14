import { Component, OnInit, WritableSignal, signal } from '@angular/core'; // Importe WritableSignal
import { HttpErrorResponse } from '@angular/common/http';
import { Api } from '../../api';
import { RouterLink } from '@angular/router';
import { Navbar } from '../../components/navbar/navbar';

type Usuario = {
  id: string;
  nome: string;
  email: string;
};

type CessionarioAdmin = {
  id?: string;
  nome: string;
};

@Component({
  selector: 'app-admin',
  standalone: true, // Adicionado caso não esteja no módulo
  imports: [Navbar, RouterLink],
  templateUrl: './admin.html',
  styleUrl: './admin.css',
})
export class Admin implements OnInit { // Adicionado 'implements OnInit' por boa prática

  // 1. Altere de Signal para WritableSignal
  usuarios: WritableSignal<Usuario[]> = signal([]);
  cessionarios: WritableSignal<CessionarioAdmin[]> = signal([]);

  constructor(private api: Api) {}

  private normalizeList<T>(payload: unknown): T[] {
    if (typeof payload === 'string') {
      try {
        const parsed = JSON.parse(payload);
        return this.normalizeList<T>(parsed);
      } catch {
        return [];
      }
    }

    if (Array.isArray(payload)) {
      return payload as T[];
    }

    if (payload && typeof payload === 'object') {
      const container = payload as { content?: unknown; data?: unknown; items?: unknown; [key: string]: unknown };

      if (Array.isArray(container.content)) return container.content as T[];
      if (Array.isArray(container.data)) return container.data as T[];
      if (Array.isArray(container.items)) return container.items as T[];

      for (const value of Object.values(container)) {
        if (Array.isArray(value)) return value as T[];
      }
    }

    return [];
  }

  private normalizeUsuarios(payload: unknown): Usuario[] {
    type RawUsuario = {
      id?: string;
      nome?: string;
      email?: string;
      firstName?: string;
      lastName?: string;
    };

    return this.normalizeList<RawUsuario>(payload).map((u) => {
      const nome = u.nome ?? [u.firstName, u.lastName].filter(Boolean).join(' ').trim();
      return {
        id: u.id ?? `${u.email ?? nome}-${Math.random().toString(36).slice(2, 9)}`,
        nome,
        email: u.email ?? '',
      };
    });
  }

  private normalizeCessionarios(payload: unknown): CessionarioAdmin[] {
    type RawCessionario = {
      id?: string;
      nome?: string;
      name?: string;
    };

    return this.normalizeList<RawCessionario>(payload).map((c) => ({
      id: c.id,
      nome: c.nome ?? c.name ?? '',
    }));
  }

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
    return body?.message ?? 'Falha ao carregar dados de admin';
  }

  ngOnInit(): void {
    const estoqueId = localStorage.getItem('estoque');

    if (!estoqueId) {
      window.alert('Estoque não identificado.');
      return;
    }

    this.api.buscarUsuariosPorEstoque(estoqueId).subscribe({
      next: (response: unknown) => {
        // 2. Use .set() para atualizar o valor do Signal
        this.usuarios.set(this.normalizeUsuarios(response));
      },
      error: (error: HttpErrorResponse) => window.alert(this.extractErrorMessage(error)),
    });

    this.api.buscarCessionariosPorEstoque(estoqueId).subscribe({
      next: (response: unknown) => {
        // 3. Use .set() para atualizar o valor do Signal
        this.cessionarios.set(this.normalizeCessionarios(response));
      },
      error: (error: HttpErrorResponse) => window.alert(this.extractErrorMessage(error)),
    });
  }
}