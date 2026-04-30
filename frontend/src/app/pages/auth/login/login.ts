import { NgClass } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from "@angular/router";
import { Api } from '../../../api';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, NgClass, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

    form: FormGroup;

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
        return;
      }

      this.api.login(this.form.value).subscribe({
        next: (res) => {
          localStorage.setItem('token', res.token);
          this.router.navigate(['/']);
        },
        error: (err) => {
          console.error('Erro ao fazer login', err);
          alert('Falha na autenticação. Verifique suas credenciais.');
        }
      });
    }

    shouldHideErrorFieldMessage(field: string): boolean {
      let control = this.form.get(field);

      if(control?.touched && control.invalid) return false;

      return true;
    }
}
