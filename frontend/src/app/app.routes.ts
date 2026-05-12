import { Routes } from '@angular/router';
import { Login } from './pages/auth/login/login';
import { Cadastro } from './pages/auth/cadastro/cadastro';
import { RedefinirSenha } from './pages/auth/redefinir-senha/redefinir-senha';
import { Products } from './pages/products/products';
import { CadastrarProduto } from './pages/cadastrar-produto/cadastrar-produto';
import { CadastrarUnidade } from './pages/cadastrar-unidade/cadastrar-unidade';
import { CadastrarCategoria } from './pages/cadastrar-categoria/cadastrar-categoria';
import { Movimentacoes } from './pages/movimentacoes/movimentacoes';
import { CadastrarMovimentacao } from './pages/cadastrar-movimentacao/cadastrar-movimentacao';
import { Admin } from './pages/admin/admin';
import { CadastrarUsuario } from './pages/cadastrar-usuario/cadastrar-usuario';
import { CadastrarCessionario } from './pages/cadastrar-cessionario/cadastrar-cessionario';
import { Relatorios } from './pages/relatorios/relatorios';

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
    path: 'movimentacoes/novo',
    component: CadastrarMovimentacao
  },
  {
    path: 'movimentacoes',
    component: Movimentacoes
  },
  {
    path: 'relatorios',
    component: Relatorios
  },
  {
    path: 'admin',
    component: Admin
  },
  {
    path: 'cadastrar-produtos',
    component: CadastrarProduto
  },
  {
    path: 'cadastrar-usuario',
    component: CadastrarUsuario
  },
  {
    path: 'cadastrar-cessionario',
    component: CadastrarCessionario
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
