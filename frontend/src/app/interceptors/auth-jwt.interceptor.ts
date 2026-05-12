import { HttpInterceptorFn } from '@angular/common/http';

const authRoutes = [
  '/api/auth/signin',
  '/api/auth/signup',
  '/api/auth/reset-password',
];

function isJwtToken(token: string | null): token is string {
  if (!token) {
    return false;
  }

  return /^[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+$/.test(token);
}

export const authJwtInterceptor: HttpInterceptorFn = (req, next) => {
  if (authRoutes.some((route) => req.url.includes(route))) {
    return next(req);
  }

  const token = localStorage.getItem('token');

  if (!isJwtToken(token)) {
    if (token) {
      localStorage.removeItem('token');
    }

    return next(req);
  }

  return next(
    req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    }),
  );
};
