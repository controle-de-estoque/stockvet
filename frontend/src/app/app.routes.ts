import { Routes } from '@angular/router';
import { Login } from './pages/auth/login/login';
import { Cadastro } from './pages/auth/cadastro/cadastro';
import { RedefinirSenha } from './pages/auth/redefinir-senha/redefinir-senha';
import { Products } from './pages/products/products';
import { CadastrarProduto } from './pages/cadastrar-produto/cadastrar-produto';
import { CadastrarUnidade } from './pages/cadastrar-unidade/cadastrar-unidade';
import { CadastrarCategoria } from './pages/cadastrar-categoria/cadastrar-categoria';

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
  },
  {
    path: 'produtos',
    component: Products
  },
  {
    path: 'cadastrar-produtos',
    component: CadastrarProduto
  },
  {
    path: 'cadastrar-unidade',
    component: CadastrarUnidade
  },
  {
    path: 'cadastrar-categoria',
    component: CadastrarCategoria
  },
  {
    path: '**',
    component: Login
  }
];
