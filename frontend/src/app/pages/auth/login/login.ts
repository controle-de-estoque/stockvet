import { Component, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from "@angular/router";
import { Api } from '../../../api';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

    form: FormGroup;
    errorMessage = signal<string>('');

    constructor(
      private fb: FormBuilder,
      private api: Api,
      private router: Router
    ) {
      this.form = fb.group({
        username: ['', [Validators.required, Validators.email]],
        password: ['', Validators.required]
      })
    }

    login() {
      if (this.form.invalid) {
        this.form.markAllAsTouched();
        this.errorMessage.set('Verifique todos os campos antes de submeter');
        return;
      }

      this.api.login(this.form.value).subscribe({
        next: (res) => {

          try {
            const payload = res.jwt.split('.')[1];
            const decoded = JSON.parse(atob(payload));
            localStorage.setItem('email', decoded.sub);
            localStorage.setItem('estoque', res.estoque);
            localStorage.setItem('token', res);
          } catch (e) {
            console.error('Token inválido');
          }

          this.router.navigate(['/produtos']);
        },
        error: (err) => {
          console.error('Erro ao fazer login', err);
          this.errorMessage.set('Falha na autenticação. Verifique suas credenciais.')
        }
      });
    }

    shouldHideErrorFieldMessage(field: string): boolean {
      let control = this.form.get(field);

      if(control?.touched && control.invalid) return false;

      return true;
    }
}
