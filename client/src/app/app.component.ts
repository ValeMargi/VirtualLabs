import { Component, OnInit } from '@angular/core';
import { ViewChild, AfterViewInit } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { MatDialogConfig, MatDialog } from '@angular/material/dialog';
import { LoginDialogComponent } from './login-dialog.component';
import { AuthService } from './auth/auth.service';
import { MatButton } from '@angular/material/button';
import { Router } from '@angular/router';
import { RegisterDialogComponent } from './register-dialog.component';
import { AddCourseDialogComponent } from './teacher/add-course-dialog.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent implements AfterViewInit, OnInit {
  @ViewChild('sidenav') sidenav: MatSidenav;
  @ViewChild('btLogin') btLogin: MatButton;
  @ViewChild('btLogout') btLogout: MatButton;

  title = 'ai20-lab05';
  loginVisibility = true;
  homeVisibility = true;
  notFoundVisibility = true;
  
  constructor(private matDialog: MatDialog, public authService: AuthService, private router: Router) {}

  ngAfterViewInit(): void {
    if (this.router.url == "") {
      this.notFoundVisibility = true;
      this.homeVisibility = false;
    }
    else {
      this.notFoundVisibility = false;

      if (this.authService.isLoggedIn()) {
        this.loginVisibility = false;

        if (this.router.url == "home") {
          this.homeVisibility = true;
        }
        else {
          this.homeVisibility = false;
        }
      }
      else {
        this.loginVisibility = true;
        this.homeVisibility = true;
      }
    }
  }

  ngOnInit() {
    
  }

  open() {
    this.sidenav.open();
  }

  close() {
    this.sidenav.close();
  }

  toggleForMenuClick() {
    this.sidenav.toggle();
  }

  openDialogLogin() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        id: 1,
        title: 'Login'
    };

    this.matDialog.open(LoginDialogComponent, dialogConfig);
  }

  openDialogRegister() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        id: 1,
        title: 'Register'
    };

    this.matDialog.open(RegisterDialogComponent, dialogConfig);
  }

  logout() {
    this.authService.logout();
  }

  openDialogCourse() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        id: 1,
        title: 'AddCourse'
    };

    this.matDialog.open(AddCourseDialogComponent, dialogConfig);
  }

}