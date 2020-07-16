import {EventEmitter, Injectable, Output} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, Observable} from "rxjs";
import { User } from '../user.model';

import * as moment from 'moment';
import { shareReplay } from 'rxjs/operators';

const API_URL_LOGIN = 'http://localhost:3000/login';    

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  @Output('userLogged') userLogged = new EventEmitter();
  user: Observable<User>;
  private userSubject: BehaviorSubject<User>;

  constructor(private http: HttpClient) {
    const user = new User();
    if (this.isLoggedIn()) {
      user.email = localStorage.getItem('email');
    }

    this.userSubject = new BehaviorSubject<User>(user);
    this.user = this.userSubject.asObservable();
  }
  
  login(email: string, password: string) {
    console.log(email);
    console.log(password);
    this.http.post(
      API_URL_LOGIN,{
        email: email,
        password:password
      }
    ).subscribe(
      (authResult: any) => {
        this.setSession(authResult, email, password);
        shareReplay();
      },
      (error: any) => {
        this.userLogged.emit(false);
      }
    );

  }

  private setSession(authResult, email, password) {
    const tkn = JSON.parse(atob(authResult.accessToken.split('.')[1]));
    console.log(atob(authResult.accessToken.split('.')[1]));
    console.log(authResult.accessToken);
    console.log("session ok");
    localStorage.setItem('token', authResult.accessToken);
    localStorage.setItem('expires_at', tkn.exp);
    localStorage.setItem('email', email);
    let user : User = new User();
    user.email = email;
    //user.password = password;
    this.userSubject.next(user);
    this.userLogged.emit(true);
  }

  logout() {
    localStorage.removeItem('expires_at');
    localStorage.removeItem('token');
    this.userSubject.next(null);    
    window.location.reload(); 
  }

  getToken(){
    const token = localStorage.getItem('token');
    if (!token)
      return '';

    return token;
  }

  public isLoggedIn() {
    return moment().isBefore(moment.unix(+localStorage.getItem('expires_at')));
  }

  public isLoggedOut() { return !this.isLoggedIn(); }

}
