import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class StudentInterceptor implements HttpInterceptor {
  constructor() {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const accessToken = localStorage.getItem('token');
    if (accessToken) {
      const cloned = request.clone({headers: request.headers.set('Authorization', 'Bearer ' + accessToken)});
      console.log('AuthInterceptor accessToken found: ' + accessToken);
      return next.handle(cloned);
    } else {
      console.log('AuthInterceptor accessToken not found');
      return next.handle(request);
    }
  }
}