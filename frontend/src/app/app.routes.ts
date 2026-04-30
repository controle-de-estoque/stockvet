import { Routes } from '@angular/router';
import { Login } from './pages/auth/login/login';
import { Cadastro } from './pages/auth/cadastro/cadastro';
import { RedefinirSenha } from './pages/auth/redefinir-senha/redefinir-senha';

export const routes: Routes = [
  {
    path: 'login',
    component: Login
  },
  {
    path: 'cadastro',
    component: Cadastro
  },
  {
    path: 'redefinir-senha',
    component: RedefinirSenha
  }
];
