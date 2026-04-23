import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { NgOptimizedImage } from '@angular/common';
import { finalize } from 'rxjs';
import { AuthApiService } from './auth-api.service';

@Component({
  selector: 'app-recover-password-page',
  imports: [ReactiveFormsModule, RouterLink, NgOptimizedImage],
  template: `
    <main class="auth-page">
      <section class="auth-form-side" aria-labelledby="recover-title">
        <article class="auth-card">
          <div class="auth-logo" aria-hidden="true">
            <img class="auth-logo-image" ngSrc="/logo.jpg" width="160" height="160" alt="" priority />
          </div>
          <h1 id="recover-title" class="auth-title">StockVet</h1>
          <p class="auth-subtitle">Portal do cliente: Juntos pelo bem estar</p>

          <form class="auth-form" [formGroup]="form" (ngSubmit)="onSubmit()" novalidate>
            <label for="email">Email</label>
            <input id="email" type="email" formControlName="email" autocomplete="email" required />
            @if (form.controls.email.touched && form.controls.email.hasError('required')) {
              <span class="field-error">Campo obrigatório</span>
            }
            @if (form.controls.email.touched && form.controls.email.hasError('email')) {
              <span class="field-error">Email inválido</span>
            }

            <label for="petName">Nome do seu primeiro animal de estimação</label>
            <input id="petName" type="text" formControlName="primeiroPet" required />
            @if (form.controls.primeiroPet.touched && form.controls.primeiroPet.hasError('required')) {
              <span class="field-error">Campo obrigatório</span>
            }

            <button class="auth-submit" type="submit" [disabled]="form.invalid || isSubmitting()">
              Validar dados
            </button>
          </form>

          @if (errorMessage()) {
            <p class="form-feedback form-feedback-error">{{ errorMessage() }}</p>
          }

          @if (successMessage()) {
            <p class="form-feedback form-feedback-success">{{ successMessage() }}</p>
          }

          <div class="auth-links">
            <a routerLink="/login">Já possui uma conta? Faça login</a>
            <a routerLink="/signup">Ainda não possui uma conta? Registre-se</a>
          </div>
        </article>
      </section>

      <aside class="auth-image-side" aria-hidden="true">
        <img
          class="auth-image"
          ngSrc="/auth-illustration.jpg"
          width="1200"
          height="1200"
          alt=""
          priority
        />
      </aside>
    </main>
  `,
  styleUrl: './auth-pages.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RecoverPasswordPageComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authApiService = inject(AuthApiService);

  protected readonly isSubmitting = signal(false);
  protected readonly errorMessage = signal('');
  protected readonly successMessage = signal('');

  protected readonly form = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    primeiroPet: ['', Validators.required]
  });

  protected onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    const payload = this.form.getRawValue();

    this.authApiService
      .recoverPassword(payload)
      .pipe(finalize(() => this.isSubmitting.set(false)))
      .subscribe({
        next: () => {
          this.successMessage.set('Solicitação de recuperação enviada com sucesso.');
          this.form.reset();
        },
        error: (error: HttpErrorResponse) => {
          if (error.status === 404) {
            this.errorMessage.set('O backend atual ainda não possui endpoint de recuperação de senha.');
            return;
          }

          this.errorMessage.set('Não foi possível iniciar a recuperação de senha agora.');
        }
      });
  }
}
