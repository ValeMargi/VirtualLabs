import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router,RouterStateSnapshot, UrlTree, ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { StudentService } from '../services/student.service';
import { TeacherService } from '../services/teacher.service';

import { AuthService } from './auth.service';
@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, 
              private studentService: StudentService,
              private teacherService: TeacherService,
              private router: Router,
              private route: ActivatedRoute){}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean {
      
      let url: string = state.url
      return this.checkLogin(url);
  }

  checkLogin(url: string): boolean {
    //se l'utente non è loggato, non si può proseguire su alcune route
    if (this.authService.isLoggedIn()) {
      let role = localStorage.getItem('role');
      let currentId = localStorage.getItem('currentId');

      //controllo che i dati fondamentali per la sessione siano valorizzati
      if (role == null || role.length == 0 || currentId == null || currentId.length == 0) {
        this.router.navigate([url], {queryParams: {doLogin : "true"}});
        return false;
      }

      //uno studente non può accedere alle route del docente e viceversa
      if ((role == "teacher" && url.indexOf("student") == 1) || (role == "student" && url.indexOf("teacher") == 1)) {
        window.alert("Non hai i privilegi per accedere a questa sezione");
        this.router.navigateByUrl("home");
        return false;
      }

      return true;
    }
    else {
      //si apre la dialog di login se ancora non è aperta
      if (!url.includes("doLogin")) {
        this.authService.storeUrl(url);
        this.authService.userLogged.emit(false);
        this.router.navigate([''], {queryParams: {doLogin : "true"}});
        return false;
      }
      else {
        return true;
      }
    }
  }
  
}
