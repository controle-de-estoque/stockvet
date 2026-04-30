import { NgClass } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from "@angular/router";
import { Api } from '../../../api';

@Component({
  selector: 'app-cadastro',
  imports: [ReactiveFormsModule, NgClass, RouterLink],
  templateUrl: './cadastro.html',
  styleUrl: './cadastro.css', // ou remova se não for usar arquivo de estilo
})
export class Cadastro {

    form: FormGroup;

    constructor(
      private fb: FormBuilder,
      private api: Api,
      private router: Router
    ) {
      this.form = fb.group({
        nome: ['', Validators.required],
        sobrenome: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        senha: ['', [Validators.required, Validators.minLength(6)]],
        animalEstimacao: ['', Validators.required]
      })
    }

    cadastrar() {
      if (this.form.invalid) {
        this.form.markAllAsTouched();
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
          alert('Erro ao realizar cadastro. Verifique os dados ou se o e-mail já está em uso.');
        }
      });
    }

    shouldHideErrorFieldMessage(field: string): boolean {
      let control = this.form.get(field);

      if(control?.touched && control.invalid) return false;

      return true;
    }
}
