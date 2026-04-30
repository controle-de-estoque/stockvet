import { NgClass } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from "@angular/router";
import { Api } from '../../../api';

@Component({
  selector: 'app-redefinir-senha',
  imports: [ReactiveFormsModule, NgClass, RouterLink],
  templateUrl: './redefinir-senha.html',
  styleUrl: './redefinir-senha.css', // ou remova se não for usar arquivo de estilo
})
export class RedefinirSenha {

    form: FormGroup;

    constructor(
      private fb: FormBuilder,
      private api: Api,
      private router: Router
    ) {
      this.form = fb.group({
        email: ['', [Validators.required, Validators.email]],
        animalEstimacao: ['', Validators.required],
        senha: ['', [Validators.required, Validators.minLength(6)]]
      })
    }

    redefinir() {
      if (this.form.invalid) {
        this.form.markAllAsTouched();
        return;
      }

      const dados = {
        email: this.form.value.email,
        firstPetName: this.form.value.animalEstimacao,
        password: this.form.value.senha
      };

      this.api.redefinirSenha(dados).subscribe({
        next: () => {
          alert('Senha redefinida com sucesso!');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          console.error('Erro ao redefinir senha', err);
          alert('E-mail ou resposta da pergunta de segurança incorretos.');
        }
      });
    }

    shouldHideErrorFieldMessage(field: string): boolean {
      let control = this.form.get(field);

      if(control?.touched && control.invalid) return false;

      return true;
    }
}
