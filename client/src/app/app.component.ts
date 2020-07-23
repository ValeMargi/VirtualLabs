import { Component, OnInit } from '@angular/core';
import { ViewChild, AfterViewInit } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { MatDialogConfig, MatDialog } from '@angular/material/dialog';
import { LoginDialogComponent } from './login-dialog.component';
import { AuthService } from './auth/auth.service';
import { MatButton } from '@angular/material/button';
import { Router, NavigationEnd } from '@angular/router';
import { RegisterDialogComponent } from './register-dialog.component';
import { AddCourseDialogComponent } from './teacher/add-course/add-course-dialog.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent implements AfterViewInit, OnInit {
  @ViewChild('sidenav') sidenav: MatSidenav;
  @ViewChild('btLogin') btLogin: MatButton;
  @ViewChild('btLogout') btLogout: MatButton;

  title = 'VirtualLabs';
  loginVisibility: boolean = true;
  homeVisibility: boolean = true;
  teacherVisibility: boolean = true;
  notFoundVisibility: boolean = true;
  course = "";
  
  constructor(private matDialog: MatDialog, public authService: AuthService, private router: Router) {
    
  }

  ngAfterViewInit(): void {
    this.authService.userLogged.subscribe(ok => {
      if (ok && this.authService.isLoggedIn()) {
        this.loginVisibility = false;

        let role = localStorage.getItem("role");
        
        if (role == "student") {
          this.teacherVisibility = false;
        }
        else {
          this.teacherVisibility = true;
        }
      }
      else {
        this.loginVisibility = true;
        this.homeVisibility = true;
        this.router.navigateByUrl("home");
      }
    });
  }

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.loginVisibility = false;
    }
    else {
      this.loginVisibility = true;
    }

    if (this.router.url == "") {
      this.notFoundVisibility = true;
    }
    else {
      this.notFoundVisibility = false;

      if (this.router.url == "/home") {
        this.homeVisibility = true;
      }
      else {
        this.homeVisibility = false;
      }
    }

    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) { 
        if (event.urlAfterRedirects == "/home") {
          this.homeVisibility = true;
        }
        else {
          this.homeVisibility = false;
        }
      }
    });
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

  get courseName() {
    if (this.router.url.length <= 1)
      return "";
      
    let res = this.router.url.split("/");

    if (res[2] != null && res[2].match("course")) {
      let res2 = res[3].split("-");
      var name = "";

      for (var n of res2) {
        name += n.charAt(0).toUpperCase() + n.slice(1) + " ";
      }

      return name;
    }
    else {
      return "";
    }
  }

  get activeTab() {
    if (this.router.url.length <= 1)
      return "";
      
    let res = this.router.url.split("/");

    if (res[4] == null)
      return 0;

    if (res[4].match("students") || res[4].match("teams")) {
      return 0; 
    }
    else if (res[4].match("vms")) {
      return 1;
    }
    else if (res[4].match("assignments")) {
      return 2;
    }
    else {
      return 0;
    }
  }

}