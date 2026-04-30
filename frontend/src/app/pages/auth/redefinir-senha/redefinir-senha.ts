import { Component, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from "@angular/router";
import { Api } from '../../../api';

@Component({
  selector: 'app-redefinir-senha',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './redefinir-senha.html',
  styleUrl: './redefinir-senha.css',
})
export class RedefinirSenha {

    form: FormGroup;
    errorMessage = signal<string>('');

    constructor(
      private fb: FormBuilder,
      private api: Api,
      private router: Router
    ) {
      this.form = fb.group({
        email: ['', [Validators.required, Validators.email]],
        animalEstimacao: ['', Validators.required],
        senha: ['', [Validators.required, Validators.minLength(8)]]
      })
    }

    redefinir() {
      if (this.form.invalid) {
        this.form.markAllAsTouched();
        this.errorMessage.set('Verifique todos os campos antes de submeter');
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
          this.errorMessage.set('E-mail ou resposta da pergunta de segurança incorretos.');
        }
      });
    }

    shouldHideErrorFieldMessage(field: string): boolean {
      let control = this.form.get(field);

      if(control?.touched && control.invalid) return false;

      return true;
    }
}
