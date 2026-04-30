import { Component, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from "@angular/router";
import { Api } from '../../../api';

@Component({
  selector: 'app-cadastro',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './cadastro.html',
  styleUrl: './cadastro.css',
})
export class Cadastro {

    form: FormGroup;
    errorMessage = signal<string>('');

    constructor(
      private fb: FormBuilder,
      private api: Api,
      private router: Router
    ) {
      this.form = fb.group({
        nome: ['', Validators.required],
        sobrenome: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        senha: ['', [Validators.required, Validators.minLength(8)]],
        animalEstimacao: ['', Validators.required]
      })
    }

    cadastrar() {
      if (this.form.invalid) {
        this.form.markAllAsTouched();
        this.errorMessage.set('Verifique todos os campos antes de submeter');
        return;
      }

      const dados = {
        firstName: this.form.value.nome,
        lastName: this.form.value.sobrenome,
        email: this.form.value.email,
        password: this.form.value.senha,
        firstPetName: this.form.value.animalEstimacao
      };

      this.api.cadastraUsuarioAdmin(dados).subscribe({
        next: () => {
          alert('Cadastro realizado com sucesso!');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          console.error('Erro ao cadastrar', err);
          this.errorMessage.set('Erro ao realizar cadastro. Verifique os dados ou se o e-mail já está em uso.');
        }
      });
    }

    shouldHideErrorFieldMessage(field: string): boolean {
      let control = this.form.get(field);

      if(control?.touched && control.invalid) return false;

      return true;
    }
}
