import { Component, OnDestroy, OnInit } from '@angular/core';
import { ViewChild, AfterViewInit } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { MatDialogConfig, MatDialog } from '@angular/material/dialog';
import { LoginDialogComponent } from './login/login-dialog.component';
import { AuthService } from './auth/auth.service';
import { MatButton } from '@angular/material/button';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
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
import { RegisterContComponent } from './register/register-cont/register-cont.component';
import { LoginContComponent } from './login/login-cont/login-cont.component';
import { Subscription } from 'rxjs/internal/Subscription';
import { EditCourseContComponent } from './teacher/edit-course/edit-course-cont/edit-course-cont.component';
import { ForgotPasswordContComponent } from './forgot-password/forgot-password-cont/forgot-password-cont.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent implements AfterViewInit, OnInit, OnDestroy {
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
  route$: Subscription;
  routeQueryParams$: Subscription;

  name: string;
  firstName: string;

  LoginSuccess: boolean = false;

  constructor(private matDialog: MatDialog,
              private courseService:  CourseService,
              private teacherService: TeacherService,
              private studentService: StudentService,
              private authService: AuthService,
              private router: Router,
              private route: ActivatedRoute) {
  }

  ngAfterViewInit(): void {

  }

  ngOnInit() {
    this.role = localStorage.getItem("role");

    if (this.router.url == "") {
      this.notFoundVisibility = true;
    }
    else {
      this.notFoundVisibility = false;

      if (this.authService.isLoggedIn()) {
        this.loginVisibility = false;
        this.LoginSuccess = true;

        if (this.role.match("student")) {
          this.teacherVisibility = false;
        }
        else {
          this.teacherVisibility = true;
        }

        this.authService.getUserByRole();
        this.getUserName(true);
        this.setCourses();
      }
      else {
        this.loginVisibility = true;
        this.getUserName(false);
      }
    }

    this.authService.userLogged.subscribe(ok => {
      if (ok && this.authService.isLoggedIn()) {
        this.loginVisibility = false;
        this.LoginSuccess = true;

        this.role = localStorage.getItem("role");

        if (this.role == "student") {
          this.teacherVisibility = false;
        }
        else {
          this.teacherVisibility = true;
        }

        this.getUserName(true);
        this.setCourses();
      }
      else {
        this.loginVisibility = true;
        this.homeVisibility = true;
        this.getUserName(false);
        this.sidenav.close();
      }
    });

    this.courseService.currentCourse.subscribe(
      (data) => {
        if (data == null || data.name == "") {
          return;
        }

        let add: boolean = true;

        this.courses.forEach(c => {
          if (c.name == data.name) {
            add = false;
          }
        });

        if (add) {
          this.courses.push(data);
          this.router.navigateByUrl(this.getRouteWithCourse(data));
        }
      },
      (error) => {

      }
    );

    this.courseService.courseRemove.subscribe(
      (data) => {
        let array: Course[] = this.courses;
        this.courses = new Array();

        array.forEach(c => {
          if (c.name != data) {
            this.courses.push(c);
          }
        });

        this.courseService.setCurrentCourse(new Course("", "", -1, -1, 0, -1, -1, -1, -1, -1));
        this.router.navigateByUrl("home");
      },
      (error) => {}

    );

    this.courseService.courseReload.subscribe(
      (data) => {
        this.setCourses();
      },
      (error) => {

      }
    )

    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        if (event.urlAfterRedirects.indexOf("home") == 1) {
          this.homeVisibility = true;
          this.notFoundVisibility = false;
        }
        else {
          this.homeVisibility = false;

          if (event.urlAfterRedirects.indexOf("page-not-found") == 1) {
            this.notFoundVisibility = true;
          }
          else {
            this.notFoundVisibility = false;

            if (this.courseService.currentCourse.getValue().name == "" && this.router.url.match("course")) {
              this.courseService.currentCourse.getValue().name = this.router.url.split("/")[3];
            }
          }
        }
      }
    });

    this.route$ = this.route.params.subscribe(params => {
      
    });

    this.routeQueryParams$ = this.route.queryParams.subscribe(params => {
      if (params['doLogin']) {
        if (this.authService.isLoggedOut()) {
          this.openDialogLogin();
        }
        else {
          const queryParams = {}
          this.router.navigate([], { queryParams, replaceUrl: true, relativeTo: this.route });
        }
      }
      else if (params['doRegister']) {
        this.openDialogRegister();
      }
      else if (params['myProfile']) {
        this.openDialogProfile();
      }
      else if (params['newCourse']) {
        this.openDialogCourse();
      }
      else if (params['editCourse']) {
        this.openCourseEdit();
      }
      else if (params['forgotPass']) {
        this.openDialogForgotPassword();
      }
    });
  }

  ngOnDestroy() {
    this.route$.unsubscribe();
    this.routeQueryParams$.unsubscribe();
  }

  getUserName(loggedIn: boolean) {
    if (loggedIn) {
      if (this.studentService.currentStudent != null){
          this.name = this.studentService.currentStudent.name;
          this.firstName = this.studentService.currentStudent.firstName;

      }
      else if(this.teacherService.currentTeacher != null){
          this.name = this.teacherService.currentTeacher.name;
          this.firstName = this.teacherService.currentTeacher.firstName;
      }
    }
    else {
      this.name = "";
      this.firstName = "";
    }
  }

  get registerSuccess() {
    if (this.router.url.indexOf("register") == 1) {
      return true;
    }
    else {
      return false;
    }
  }

  get passwordReset() {
    if (this.router.url.indexOf("user") == 1) {
      return true;
    }
    else {
      return false;
    }
  }

  setCourses() {
    if (this.role.match("student")) {
      this.studentService.getCourses(this.studentService.currentStudent.id).subscribe(
        (data) => {
          this.courses = data;
        },
        (error) => {

        }
      );
    }
    else {
      this.teacherService.getCoursesForProfessor(this.teacherService.currentTeacher.id).subscribe(
        (data) => {
          this.courses = data;
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

  routeToLogin() {
    this.router.navigate([], {queryParams: {doLogin : "true"}});
  }

  routeToRegister() {
    this.router.navigate([], {queryParams: {doRegister : "true"}});
  }

  routeNewCourse() {
    this.router.navigate([], {queryParams: {newCourse : "true"}});
  }

  routeMyProfile() {
    this.router.navigate([], {queryParams: {myProfile : "true"}});
  }

  routeCourseEdit() {
    this.router.navigate([], {queryParams: {editCourse : "true"}});
  }

  openDialogLogin(): void {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        id: 1,
        title: 'Login'
    };

    const dialogRef = this.matDialog.open(LoginContComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(result => {
      const queryParams = {}
      const url = this.authService.getStoredUrl();
      this.authService.storeUrl(null);

      if (url != null && this.authService.isLoggedIn()) {
        this.router.navigateByUrl(url);
      }
      else {
        const params = this.route.snapshot.queryParams;

        if (!params['doRegister'] && !params['forgotPass']) {
          this.router.navigate(["home"], { queryParams, replaceUrl: true, relativeTo: this.route });
        }
      }
    });
  }

  openDialogRegister() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '50%';


    dialogConfig.data = {
        id: 1,
        title: 'Register'
    };

    const dialogRef = this.matDialog.open(RegisterContComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(result => {
      const queryParams = {}
      this.router.navigate([], { queryParams, replaceUrl: true, relativeTo: this.route });
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigateByUrl("home");
  }

  openDialogCourse() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = "30%";

    dialogConfig.data = {
        id: 1,
        title: 'AddCourse'
    };

    const dialogRef = this.matDialog.open(AddCourseContComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(result => {
      const queryParams = {}
      this.router.navigate([], { queryParams, replaceUrl: true, relativeTo: this.route });
    });
  }

  openDialogProfile() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.minWidth = "30%";

    dialogConfig.data = {
        id: 1,
        title: 'Profile'
    };


    const dialogRef = this.matDialog.open(EditProfileContComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(result => {
      const queryParams = {}
      this.router.navigate([], { queryParams, replaceUrl: true, relativeTo: this.route });
    });
  }

  openCourseEdit() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        id: 1,
        title: 'CourseEdit'
    };


    const dialogRef =  this.matDialog.open(EditCourseContComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(result => {
      const queryParams = {}
      this.router.navigate([], { queryParams, replaceUrl: true, relativeTo: this.route });
    });
  }

  openDialogForgotPassword() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        id: 1,
        title: 'ForgotPwd'
    };

    const dialogRef = this.matDialog.open(ForgotPasswordContComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(result => {
      const queryParams = {}
      this.router.navigate([], { queryParams, replaceUrl: true, relativeTo: this.route });
    });
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
    if (value.length <= 1 || value.indexOf("home") == 1) {
      return "";
    }

    let res = value.split("/");
    let res2;
    var name = "";

    if (res[2] != null && res[2].match("course")) {
      res2 = res[3].split("-").join(' ');
    }
    else {
      res2 = value.split("-").join(' ');
    }

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
}
