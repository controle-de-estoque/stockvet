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

  cadastrarCategoria(dados: {nome: string, estoque: string}): Observable<string> {
    return this.http.post(`${this.baseUrl}/produtos/categoria`, dados, { responseType: 'text' });
  }

  cadastrarUnidade(dados: {nome: string, consumoMinimo: number, estoque: string}): Observable<any> {
    return this.http.post(`${this.baseUrl}/produtos/unidade`, dados, { responseType: 'text' });
  }

  buscarCategoriasPorEstoque(): Observable<any> {
    return this.http.get(`${this.baseUrl}/produtos/categoria/${localStorage.getItem('estoque')}`);
  }

  buscarUnidadesPorEstoque(): Observable<any> {
    return this.http.get(`${this.baseUrl}/produtos/unidade/${localStorage.getItem('estoque')}`);
  }

  cadastrarProduto(dados: {nome: string, categoria: string, tipo: string, unidade: string, estoque: string}) {
    return this.http.post(`${this.baseUrl}/produtos`, dados, {responseType: 'text'});
  }

  desativarProduto(id: string) {
    return this.http.patch(`${this.baseUrl}/produtos/desativar/${id}`, null, { responseType: 'text' });
  }
}
