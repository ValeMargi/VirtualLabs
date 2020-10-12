import {EventEmitter, Injectable, Output} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {BehaviorSubject, Observable} from "rxjs";
import { User } from '../models/user.model';

import * as jwt_decode from "node_modules/jwt-decode";

import * as moment from 'moment';
import { shareReplay } from 'rxjs/operators';
import { Student } from '../models/student.model';
import { TeacherService } from '../services/teacher.service';
import { StudentService } from '../services/student.service';
import { Teacher } from '../models/teacher.model';   

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  API_AUTH = 'http://localhost:8080/API';

  @Output('userLogged') userLogged = new EventEmitter();
  user: Observable<User>;
  
  currentUser: User;

  constructor(private http: HttpClient, private teacherService: TeacherService, private studentService: StudentService) {
    
  }
  
  login(email: string, password: string) {
    //console.log(email);
    //console.log(password);
    this.http.post(`http://localhost:8080/login`,{
        "username": email,
        "password": password
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
    //console.log(JSON.stringify(authResult));
    const tkn = JSON.parse(atob(authResult.token.split('.')[1]));
    //console.log(atob(authResult.token.split('.')[1]));
    //console.log(authResult.token);
    let token = jwt_decode(authResult.token);
    console.log(token);

    let role = token.role;

    if (role == "professor") {
      role = "teacher";
    }

    this.currentUser = new User(-1, "", role, true);

    this.setUserByRole(token);

    localStorage.setItem('token', authResult.token);
    localStorage.setItem('expires_at', tkn.exp);
    localStorage.setItem('role', role);

    this.userLogged.emit(true);
  }

  logout() {
    localStorage.removeItem('expires_at');
    localStorage.removeItem('token');   
    localStorage.removeItem('role');
    localStorage.removeItem('currentId');
    this.currentUser = null;
    this.studentService.currentStudent = null;
    this.teacherService.currentTeacher = null;
    this.userLogged.emit(false);
  }

  public isLoggedIn() {

    return moment().isBefore(moment.unix(+localStorage.getItem('expires_at')));
  }

  public isLoggedOut() { return !this.isLoggedIn(); }

  getUserByRole() {
    if (this.currentUser == null) {
      this.currentUser = new User(-1, "", localStorage.getItem('role'), true);
    }

    if (this.currentUser.role == "student") {
      if (this.studentService.currentStudent == null) {
        let token = jwt_decode(localStorage.getItem('token'));
        this.setUserByRole(token);
      }

      return this.studentService.currentStudent;
    }
    else {
      if (this.teacherService.currentTeacher == null) {
        let token = jwt_decode(localStorage.getItem('token'));
        this.setUserByRole(token);
      }

      return this.teacherService.currentTeacher;
    }
  }

  setUserByRole(token: any) {
    if (token.role == "student") {
      this.studentService.currentStudent = new Student(token.id, token.firstname, token.name, token.id + "@studenti.polito.it");
    }
    else {
      this.teacherService.currentTeacher = new Teacher(token.id, token.firstname, token.name, token.id + "@polito.it");
    }

    localStorage.setItem('currentId', token.id);
  }

  registerUser(file: File, userJson: any) {
    if (file == null) {
       return null;
    }

    console.log(userJson)
    
    let data: FormData = new FormData();
    data.append('file', file, file.name);

    data.append('registerData', new Blob([JSON.stringify(userJson)], {
      type: "application/json" }));

    return this.http.post<User>(`${this.API_AUTH}/addUser`, data);
  }

  confirmationPage(token: string) {
    return this.http.get<boolean>(`${this.API_AUTH}/registration/confirm/${token}`);
  }

  resetPassword(userEmail: string) {
    return this.http.post(`${this.API_AUTH}/user/resetPassword`, userEmail);
  }

  showChangePasswordPage() {
    return this.http.get<string>(`${this.API_AUTH}/user/changePassword`);
  }

  savePassword(newPassword: any) {
    //JSON {token: token, password: password}
    return this.http.post<boolean>(`${this.API_AUTH}/user/savePassword`, newPassword);
  }

  changeUserPassword(newPassword: Map<string, string>) {
    //Map<Vecchia Password, Nuova Password>
    return this.http.post<boolean>(`${this.API_AUTH}/user/updatePassword`, newPassword);
  }

  changeAvatar(file: File) {
    if (file == null)
       return null;

    let data: FormData = new FormData();
    data.append("file", file, file.name);

    return this.http.post<boolean>(`${this.API_AUTH}/user/updateAvatar`, data)
  }

}
