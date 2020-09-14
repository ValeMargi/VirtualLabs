import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router,RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';

import { AuthService } from './auth.service';
@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router){}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
      
      if (this.authService.isLoggedIn()) {
        let role = localStorage.getItem('role');

        /*if (role == null || role.length == 0 || this.router.url.length <= 1 || this.router.url.match("home")) {
          return false;
        }*/

        /*console.log(this.router.url.split("/")[2]);

        if ((role.match("teacher") && this.router.url.match("teacher")) || (role.match("student") && this.router.url.match("student"))) {
          return true;
        }
        else {
          console.log(this.router.url.concat("not-allowed"))
          this.router.navigateByUrl(this.router.url.concat("not-allowed"));
          return false;
        }*/

        return true;
      }
      else {
        return this.router.parseUrl("/login");
      }
  }
  
}
