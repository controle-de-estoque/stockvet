import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

type FuncaoPayload = {
  name: 'USUARIO';
};

export type LoginRequest = {
  email: string;
  senha: string;
};

export type LoginResponse = {
  token: string;
};

export type SignupRequest = {
  nome: string;
  sobrenome: string;
  email: string;
  senha: string;
  primeiroPet: string;
};

type SignupBackendRequest = {
  nome: string;
  sobrenome: string;
  email: string;
  senha: string;
  primeiroPet: string;
  funcao: FuncaoPayload;
};

export type RecoverPasswordRequest = {
  email: string;
  primeiroPet: string;
};

@Injectable({ providedIn: 'root' })
export class AuthApiService {
  private readonly http = inject(HttpClient);
  private readonly authBasePath = '/api/autent';

  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.authBasePath}/Login`, payload);
  }

  signup(payload: SignupRequest): Observable<void> {
    const backendPayload: SignupBackendRequest = {
      nome: payload.nome,
      sobrenome: payload.sobrenome,
      email: payload.email,
      senha: payload.senha,
      primeiroPet: payload.primeiroPet,
      funcao: {
        name: 'USUARIO'
      }
    };

    return this.http.post<void>(`${this.authBasePath}/registrador`, backendPayload);
  }

  recoverPassword(payload: RecoverPasswordRequest): Observable<void> {
    return this.http.post<void>(`${this.authBasePath}/recuperar-senha`, {
      email: payload.email,
      pergunta: payload.primeiroPet
    });
  }
}
