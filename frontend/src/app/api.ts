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
    return this.http.post(`${this.baseUrl}/categorias`, dados, { responseType: 'text' });
  }

  cadastrarCessionario(dados: {nome: string, email: string, estoque: string}): Observable<any> {
    return this.http.post(`${this.baseUrl}/cessionarios`, dados, { responseType: 'text' });
  }

  cadastrarUsuario(dados: {firstName: string, lastName: string, email: string, password: string, firstPetName: string, estoque: string}): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/register-simple-user`, dados, { responseType: 'text' });
  }

  cadastrarUnidade(dados: {nome: string, consumoMinimo: number, estoque: string}): Observable<any> {
    return this.http.post(`${this.baseUrl}/unidades`, dados, { responseType: 'text' });
  }

  buscarCategoriasPorEstoque(): Observable<any> {
    return this.http.get(`${this.baseUrl}/categorias/${localStorage.getItem('estoque')}`);
  }

  buscarUnidadesPorEstoque(): Observable<any> {
    return this.http.get(`${this.baseUrl}/unidades/${localStorage.getItem('estoque')}`);
  }

  buscarCessionariosPorEstoque(estoque: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/cessionarios/${estoque}`);
  }

  buscarUsuariosPorEstoque(estoque: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/auth/users/${estoque}`);
  }

  cadastrarProduto(dados: {nome: string, categoria: string, tipo: string, unidade: string, estoque: string, quantidadeCritica: number}) {
    return this.http.post(`${this.baseUrl}/produtos`, dados, {responseType: 'text'});
  }

  buscarMovimentacoes(): Observable<any> {
    return this.http.get(`${this.baseUrl}/movimentacoes/${localStorage.getItem('estoque')}`);
  }

  desativarProduto(id: string) {
    return this.http.patch(`${this.baseUrl}/produtos/desativar/${id}`, null, { responseType: 'text' });
  }

  cadastrarMovimentacoesEntrada(dados: Array<{produto: string, estoque: string, movimentadoPor: string, quantidade: string, dataHoraMovimentacao: string, tipo: string, loteId: string, dataValidade: string}>) {
    return this.http.post(`${this.baseUrl}/movimentacoes/entrada`, dados);
  }

  cadastrarMovimentacoesSaida(dados: Array<{produto: string, estoque: string, movimentadoPor: string, quantidade: string, dataHoraMovimentacao: string, tipo: string, loteId: string, dataValidade: string}>) {
    return this.http.post(`${this.baseUrl}/movimentacoes/saida`, dados);
  }

  getIdFromUserEmail() {
    return this.http.get(`${this.baseUrl}/auth/users/uuid/` + localStorage.getItem('email')!, { responseType: 'text' });
  }

}
