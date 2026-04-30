import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class Api {

  private baseUrl = "http://localhost:8080/api/auth";

  constructor(private http: HttpClient) {}

  login(dados: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/signin`, dados, {responseType: 'text'});
  }

  cadastraUsuarioAdmin(dados: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/signup`, dados, {responseType: 'text'});
  }

  redefinirSenha(dados: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/reset-password`, dados, {responseType: 'text'});
  }
}
