import { Component, OnInit } from '@angular/core';
import { ViewChild, AfterViewInit } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { MatDialogConfig, MatDialog } from '@angular/material/dialog';
import { LoginDialogComponent } from './login/login-dialog.component';
import { AuthService } from './auth/auth.service';
import { MatButton } from '@angular/material/button';
import { Router, NavigationEnd } from '@angular/router';
import { RegisterDialogComponent } from './register/register-dialog.component';
import { AddCourseDialogComponent } from './teacher/add-course/add-course-dialog.component';
import { StudentsContComponent } from './teacher/students/students-cont.component';
import { TeamsContComponent } from './student/teams/teams-cont/teams-cont.component';
import { VmsComponent as VmsComponentTeacher } from './teacher/vms/vms.component';
import { VmsContComponent as VmsContComponentTeacher } from './teacher/vms/vms-cont.component';
import { VmsComponent as VmsComponentStudent } from './student/vms/vms.component';
import { VmsContComponent as VmsContComponentStudent } from './student/vms/vms-cont/vms-cont.component';
import { AssignmentsComponent as AssignmentsComponentTeacher } from './teacher/assignments/assignments.component';
import { AssignmentsContComponent as AssignmentsContComponentTeacher } from './teacher/assignments/assignments-cont.component';
import { AssignmentsComponent as AssignmentsComponentStudent } from './student/assignments/assignments.component';
import { AssignmentsContComponent as AssignmentsContComponentStudent } from './student/assignments/assignments-cont/assignments-cont.component';
import { EditProfileContComponent } from './edit-profile/edit-profile-cont.component';

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
  courseSelected: string = "";
  role: string = "";

  courses = ["Applicazioni internet", "Programmazione di sistema"];
  
  constructor(private matDialog: MatDialog, public authService: AuthService, private router: Router) {
    
  }

  ngAfterViewInit(): void {
    this.authService.userLogged.subscribe(ok => {
      if (ok && this.authService.isLoggedIn()) {
        this.loginVisibility = false;

        this.role = localStorage.getItem("role");
        
        if (this.role == "student") {
          this.teacherVisibility = false;
        }
        else {
          this.teacherVisibility = true;
        }
      }
      else {
        this.loginVisibility = true;
        this.homeVisibility = true;
        this.sidenav.close();
        this.router.navigateByUrl("home");
      }
    });
  }

  ngOnInit() {
    this.role = localStorage.getItem("role");

    for (let c of this.courses) {
      this.courseSelected = this.setCourseForRoute(c);
      let path: string = "teacher/course/" + this.courseSelected;
      this.router.config.push({ path: path + "/students", component: StudentsContComponent });
      this.router.config.push({ path: path + "/vms", component: VmsContComponentTeacher });
      this.router.config.push({ path: path + "/assignments", component: AssignmentsContComponentTeacher });

      path = "student/course/" + this.courseSelected;
      this.router.config.push({ path: path + "/teams", component: TeamsContComponent });
      this.router.config.push({ path: path + "/vms", component: VmsContComponentStudent });
      this.router.config.push({ path: path + "/assignments", component: AssignmentsContComponentStudent });
    }

    this.router.config.push({ path: '**', redirectTo: 'page-not-found' });

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

    if (this.role.match("student")) {
      this.teacherVisibility = false;
    }
    else {
      this.teacherVisibility = true;
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

  openDialogProfile() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        id: 1,
        title: 'Profile'
    };

    this.matDialog.open(EditProfileContComponent, dialogConfig);
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

  getRouteWithCourse(course: string) {
    this.courseSelected = this.setCourseForRoute(course);
    let res: string = this.role + "/course/" + this.courseSelected;

    if (this.role.match("student"))
      return res + "/teams";
    else
      return res + "/students";
  }

  getRoute(position: number) {
    let res: string = this.role + "/course/" + this.router.url.split("/")[3];

    if (position == 0) {
      if (this.role.match("student"))
        return res + "/teams";
      else
        return res + "/students";
    }
    else if (position == 1) {
      return res + "/vms";
    }
    else if (position == 2) {
      return res + "/assignments";
    }
  }

  setCourseForRoute(course: string): string {
    return course.toLowerCase().split(' ').join('-');
  }
}