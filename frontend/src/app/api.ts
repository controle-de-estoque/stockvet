import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class Api {

  private baseUrl = "http://localhost:8080/api";

  constructor(private http: HttpClient) {}

  login(dados: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/signin`, dados);
  }

  cadastraUsuarioAdmin(dados: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/signup`, dados, {responseType: 'text'});
  }

  redefinirSenha(dados: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/reset-password`, dados, {responseType: 'text'});
  }

  buscarProdutos(): Observable<any> {
    return this.http.get(`${this.baseUrl}/produtos/${localStorage.getItem('estoque')}`);
  }

  cadastrarCategoria(dados: {nome: string, estoque: string}): Observable<any> {
    return this.http.post(`${this.baseUrl}/produtos/categoria`, dados);
  }

  cadastrarUnidade(dados: {nome: string, consumoMinimo: number, estoque: string}): Observable<any> {
    return this.http.post(`${this.baseUrl}/produtos/unidade`, dados);
  }
}
