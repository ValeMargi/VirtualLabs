import {EventEmitter, Injectable, Output} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, Observable} from "rxjs";
import { User } from '../models/user.model';

import * as moment from 'moment';
import { shareReplay } from 'rxjs/operators';
import { Student } from '../models/student.model';

const API_URL_LOGIN = 'http://localhost:3000/login';
const API_URL_USERS = 'http://localhost:3000/users';    

const API_LOGIN = 'http://localhost:8080/login';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  @Output('userLogged') userLogged = new EventEmitter();
  user: Observable<User>;

  constructor(private http: HttpClient) {
    
  }
  
  login(email: string, password: string) {
    console.log(email);
    console.log(password);
    this.http.post(
      API_URL_LOGIN,{
        email: email,
        password: password
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
    console.log("Loggato")
    console.log(JSON.stringify(authResult));
    const tkn = JSON.parse(atob(authResult.accessToken.split('.')[1]));
    console.log(atob(authResult.accessToken.split('.')[1]));
    console.log(authResult.accessToken);
    console.log("session ok");
    localStorage.setItem('token', authResult.accessToken);
    localStorage.setItem('expires_at', tkn.exp);
    localStorage.setItem('email', email);
    
    this.getUser(email).subscribe(
      (data) => {
        //console.log(data);
        //let user: User = new User();
        let userJson = JSON.stringify(data);
        JSON.parse(userJson, (key, value) => {
          if (key == "role") {
            if (value == "student") {
              console.log("student")
            }
            else if (value == "teacher") {
              console.log("teacher")
            }

            localStorage.setItem("role", value);
            this.userLogged.emit(true);
          }
        })
      },
      (error: any) => {
        this.userLogged.emit(false);
      }
    );
  }

  logout() {
    localStorage.removeItem('expires_at');
    localStorage.removeItem('token');   
    this.userLogged.emit(false);
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

  getUser(email: string) {
    return this.http.get<User>(`${API_URL_USERS}?email=${email}`);
  }

}
