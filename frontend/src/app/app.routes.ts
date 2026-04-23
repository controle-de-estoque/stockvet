import { Routes } from '@angular/router';

export const routes: Routes = [
	{
		path: 'login',
		loadComponent: () =>
			import('./pages/auth/login.page').then((m) => m.LoginPageComponent)
	},
	{
		path: 'signup',
		loadComponent: () =>
			import('./pages/auth/signup.page').then((m) => m.SignupPageComponent)
	},
	{
		path: 'recover-password',
		loadComponent: () =>
			import('./pages/auth/recover-password.page').then(
				(m) => m.RecoverPasswordPageComponent
			)
	},
	{
		path: '',
		pathMatch: 'full',
		redirectTo: 'login'
	},
	{
		path: '**',
		redirectTo: 'login'
	}
];
