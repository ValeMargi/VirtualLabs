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
      
      let url: string = state.url;
      return this.checkLogin(url);
  }

  checkLogin(url: string): boolean {
    if (this.authService.isLoggedIn()) {
      let role = localStorage.getItem('role');
      let currentId = localStorage.getItem('currentId');

      if (role == null || role.length == 0 || currentId == null || currentId.length == 0) {
        this.router.navigate([url, {queryParams: { doLogin: true }}]);
        return false;
      }

      if ((role.match("teacher") && currentId.startsWith("d")) || (role.match("student") && currentId.startsWith("s"))) {
        return true;
      }
      else {
        this.router.navigateByUrl(url.concat("/not-allowed"));
        return false;
      }
    }
    else {
      this.router.navigate([url, {queryParams: { doLogin: true }}]);
      return false;
    }
  }
  
}
