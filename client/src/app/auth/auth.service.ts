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

  //event emitter per segnalare avvenuto login/logout
  @Output('userLogged') userLogged = new EventEmitter<boolean>();
  user: Observable<User>;

  private currentUser: User;
  private storedUrl: string;

  constructor(private http: HttpClient,
    private teacherService: TeacherService,
    private studentService: StudentService) {

  }

  login(email: string, password: string) {
    return this.http.post(`http://localhost:8080/login`,
    { "username": email, "password": password });
  }

  //set dei parametri indispensabili per la sessione utente
  setSession(authResult) {
    const tkn = JSON.parse(atob(authResult.token.split('.')[1]));
    let token = jwt_decode(authResult.token);

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
    shareReplay();
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

  isLoggedIn() {
    return moment().isBefore(moment.unix(+localStorage.getItem('expires_at')));
  }

  isLoggedOut() { return !this.isLoggedIn(); }

  storeUrl(url: string) {
    //si salva la url da utilizzare per proseguire dopo una sessione scaduta
    this.storedUrl = url;
  }

  getStoredUrl() {
    return this.storedUrl;
  }

  getUserByRole() {
    if (this.currentUser == null) {
      this.currentUser = new User(-1, "", localStorage.getItem('role'), true);
    }

    if (this.currentUser.role == "student") {
      if (this.studentService.currentStudent == null) {
        let token = jwt_decode(localStorage.getItem('token'));
        this.setUserByRole(token);
      }

      this.userLogged.emit(true);
      return this.studentService.currentStudent;
    }
    else {
      if (this.teacherService.currentTeacher == null) {
        let token = jwt_decode(localStorage.getItem('token'));
        this.setUserByRole(token);
      }

      this.userLogged.emit(true);
      return this.teacherService.currentTeacher;
    }
  }

  setUserByRole(token: any) {
    //in base al ruolo, viene definito l'oggetto nell'opportuno service
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

    let data: FormData = new FormData();
    data.append('file', file, file.name);
    data.append('registerData', new Blob([JSON.stringify(userJson)], {
      type: "application/json" }));

    return this.http.post<User>(`${this.API_AUTH}/addUser`, data);
  }

  resetPassword(email: string) {
    return this.http.post<boolean>(`${this.API_AUTH}/user/resetPassword`, email);
  }

  savePassword(token: string, newPassword: string) {
    //JSON {"token": token, "newPassword": newPassword}
    return this.http.post<boolean>(`${this.API_AUTH}/user/savePassword`,
                {"token": token, "newPassword": newPassword});
  }

  changeUserPassword(oldPassword: string, newPassword: string) {
    return this.http.post<boolean>(`${this.API_AUTH}/user/updatePassword`,
                {"oldPassword": oldPassword, "newPassword": newPassword});
  }

  changeAvatar(file: File) {
    if (file == null)
       return null;

    let data: FormData = new FormData();
    data.append("file", file, file.name);

    return this.http.post<boolean>(`${this.API_AUTH}/user/updateAvatar`, data)
  }

}
