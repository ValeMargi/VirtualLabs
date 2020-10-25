import { TeacherService } from 'src/app/services/teacher.service';
import { Component, OnDestroy, OnInit, AfterViewChecked, AfterViewInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { Subscription } from 'rxjs/internal/Subscription';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { LoginDialogComponent } from '../login/login-dialog.component';
import { LoginContComponent } from '../login/login-cont/login-cont.component';
import { StudentService } from '../services/student.service';
import { Student } from '../models/student.model';
import { Teacher } from '../models/teacher.model';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy, AfterViewInit {

  private route$: Subscription;
  askLoginVisibility = true;

  name: string;
  firstName: string;

  constructor(private matDialog: MatDialog,
              private authService: AuthService,
              private route: ActivatedRoute,
              private studentService: StudentService,
              private teacherService: TeacherService) { }

  ngOnInit(): void {

    this.getUserName();
    this.route$ = this.route.queryParams.subscribe(params => {
      let login = params['doLogin'];

      if (login == 'true') {
        this.openDialogLogin();
      }
    });
  }

  ngAfterViewInit(){
    this.getUserName();
  }


  getUserName(){
    this.authService.userLogged.subscribe(
      (data) => {
        if (data == true) {
          //this.askLoginVisibility = false;

          if(this.studentService.currentStudent != null){
              this.name = this.studentService.currentStudent.name;
              this.firstName = this.studentService.currentStudent.firstName;

          }else if(this.teacherService.currentTeacher != null){
              this.name = this.teacherService.currentTeacher.name;
              this.firstName = this.teacherService.currentTeacher.firstName;
           }
        }
        else {
          //this.askLoginVisibility = true;
        }
      },
      (error) => {

      }
    );
  }


  openDialogLogin() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        id: 1,
        title: 'Login'

    };

    this.matDialog.open(LoginContComponent, dialogConfig);
  }

  ngOnDestroy() {
    if (this.route$ != null) {
      this.route$.unsubscribe();
    }
  }

}
