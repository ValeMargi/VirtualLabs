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
import { AddCourseContComponent } from './teacher/add-course/add-course-cont.component';
import { AuthGuard} from './auth/auth.guard';
import { CourseService } from './services/course.service';
import { Course } from './models/course.model';
import { TeacherService } from './services/teacher.service';
import { StudentService } from './services/student.service';
import { HomeworksContComponent as HomeworksContComponentTeacher } from './teacher/assignments/homeworks-cont.component';
import { HomeworksComponent as HomeworksComponentTeacher } from './teacher/assignments/homeworks.component';
import { VersionsContComponent as VersionsContComponentTeacher } from './teacher/assignments/versions-cont.component';
import { VersionsComponent as VersionsComponentTeacher } from './teacher/assignments/versions.component';


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

  courses: Course[] = [];
  
  constructor(private matDialog: MatDialog, 
              private courseService:  CourseService,     
              private teacherService: TeacherService,  
              private studentService: StudentService,   
              private authService: AuthService, 
              private router: Router) {
    
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

        this.setCourses();
      }
      else {
        localStorage.removeItem('token');
        localStorage.removeItem('expires_at');
        localStorage.removeItem('role');
        this.loginVisibility = true;
        this.homeVisibility = true;
        this.sidenav.close();
        this.router.navigateByUrl("home");
      }
    });
  }

  ngOnInit() {
    this.role = localStorage.getItem("role");

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

      if (this.authService.isLoggedIn()) {
        this.loginVisibility = false;

        if (this.role.match("student")) {
          this.teacherVisibility = false;

          this.studentService.getOne(localStorage.getItem('currentId')).subscribe(
            (data) => {
              console.log("ottenuto studente");
              this.studentService.currentStudent = data.student;
              this.studentService.currentAvatar = data.avatar;
              this.setCourses();
            },
            (error) => {
              console.log("Impossibile ottenere lo studente");
            }
          );
        }
        else {
          this.teacherVisibility = true;

          this.teacherService.getOne(localStorage.getItem('currentId')).subscribe(
            (data) => {
              console.log("ottenuto prof");
              this.teacherService.currentTeacher = data.professor;
              this.teacherService.currentAvatar = data.avatar;
              this.setCourses();
            },
            (error) => {
              console.log("Impossibile ottenere il professore");
            }
          );
        }
      }
      else {
        this.loginVisibility = true;
      }
    }

    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) { 
        if (event.urlAfterRedirects == "/home") {
          this.homeVisibility = true;
        }
        else {
          this.homeVisibility = false;

          if (this.courseService.currentCourse.getValue().name == "" && this.router.url.match("course")) {
            this.courseService.currentCourse.getValue().name = this.router.url.split("/")[3];
          }
        }
      }
    });

    this.courseService.currentCourse.subscribe(
      (data) => {
        if (this.courses.indexOf(data) < 0 && data.name != "") {
          this.courses.push(data);
          this.router.navigateByUrl(this.getRouteWithCourse(data));
        }
      }, 
      (error) => {

      }
    );
  }

  setRoutes() {
    for (let c of this.courses) {
      this.courseSelected = this.setCourseForRoute(c.name);
      let path: string = "teacher/course/" + this.courseSelected;
      this.router.config.push({ path: path + "/students", component: StudentsContComponent});
      this.router.config.push({ path: path + "/vms", component: VmsContComponentTeacher });
      this.router.config.push({ path: path + "/assignments", component: AssignmentsContComponentTeacher,
      children: [
        { path: '/:id/homeworks', component: HomeworksContComponentTeacher,
          children: [
            { path: '/:id/versions', component: VersionsContComponentTeacher }
            ],
        }
        ],  
      });

      path = "student/course/" + this.courseSelected;
      this.router.config.push({ path: path + "/teams", component: TeamsContComponent });
      this.router.config.push({ path: path + "/vms", component: VmsContComponentStudent });
      this.router.config.push({ path: path + "/assignments", component: AssignmentsContComponentStudent,
      /*children: [
        { path: '/:id/homeworks', component: HomeworksContComponentStudent,
          children: [
            { path: '/:id/versions', component: VersionsContComponentStudent }
            ],
        }
        ],  */
      });
    }

    this.router.config.push({ path: '**', redirectTo: 'page-not-found' });
  }

  setCourses() {
    if (this.role.match("student")) {
      this.studentService.getCourses(this.studentService.currentStudent.id).subscribe(
        (data) => {
          this.courses = data;
          //this.setRoutes();
        },
        (error) => {

        }
      );
    }
    else {
      this.teacherService.getCoursesForProfessor(this.teacherService.currentTeacher.id).subscribe(
        (data) => {
          this.courses = data;
          //this.setRoutes();
        },
        (error) => {

        }
      );
    }
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
    dialogConfig.height = '70%';

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
    dialogConfig.width = '40%';

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

    this.matDialog.open(AddCourseContComponent, dialogConfig);
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
    return this.getCourseName(this.router.url);
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

  getCourseName(value: string) {
    if (value.length <= 1)
      return "";
      
    let res = value.split("/");
    let res2;
    var name = "";

    if (res[2] != null && res[2].match("course")) {
      res2 = res[3].split("-").join(' ');
    }
    else {
      res2 = value.split("-").join(' ');
    }

    /*for (var n of res2) {
      name += n.charAt(0).toUpperCase() + n.slice(1);
    }*/

    name = res2.charAt(0).toUpperCase() + res2.slice(1);

    return name;
  }

  getRouteWithCourse(course: Course) {
    //this.courseService.setCurrentCourse(course);
    this.courseSelected = this.setCourseForRoute(course.name);
    let res: string = this.role + "/course/" + this.courseSelected;

    if (this.role.match("student"))
      return res + "/teams";
    else
      return res + "/students";
  }

  getRoute(position: number) {
    if (this.authService.isLoggedOut()) {
      return;
    }

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

  setCurrentCourse(course: Course) {
    this.courseService.currentCourse.next(course);
  }

  deleteCourse() {
    this.courseService.removeCourse(this.courseService.currentCourse.getValue().name).subscribe(
      (data) => {
        this.courses.splice(this.courses.indexOf(this.courseService.currentCourse.getValue()));
      }, 
      (error) => {
        console.log("Errore nell'eliminazione del corso");
      }
    )
  }

  enableCourse() {
    let course = this.courseService.currentCourse.getValue();
    let enabled = (course.enabled) == 0 ? false : true;

    this.courseService.enableCourse(course.name, !enabled).subscribe(
      (data) => {
        course.enabled = (!enabled) ? 1 : 0;
        this.courseService.setCurrentCourse(course);
      },
      (error) => {
        console.log("Impossibile abilitare/disabilitare il corso");
      }
    ) 
  }
}