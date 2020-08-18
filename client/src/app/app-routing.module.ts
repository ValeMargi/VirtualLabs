import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { StudentsContComponent } from './teacher/students/students-cont.component';

import { HomeComponent } from './home/home.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';

import { AssignmentsContComponent as AssignmentsContComponentTeacher } from './teacher/assignments/assignments-cont.component';
import { AssignmentsContComponent as AssignmentsContComponentStudent } from './student/assignments/assignments-cont/assignments-cont.component';

import { VmsContComponent as VmsContComponentTeacher } from './teacher/vms/vms-cont.component';
import { VmsContComponent as VmsContComponentStudent } from './student/vms/vms-cont/vms-cont.component';

import { TeamsContComponent } from './student/teams/teams-cont/teams-cont.component';
import { RegisterSuccessComponent } from './register-success/register-success.component';
import { AuthGuard} from './auth/auth.guard';
import {LoginDialogComponent} from './login/login-dialog.component';

/*
const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'page-not-found', component: PageNotFoundComponent },
  { path: 'register_success', component: RegisterSuccessComponent },
 // { path: 'teacher/course/applicazioni-internet/students', component: StudentsContComponent },
  { path: 'teacher/course/applicazioni-internet/vms', component: VmsContComponentTeacher },
  { path: 'teacher/course/applicazioni-internet/assignments', component: AssignmentsContComponentTeacher },
  { path: 'student/course/applicazioni-internet/teams', component: TeamsContComponent },
  { path: 'student/course/applicazioni-internet/vms', component: VmsContComponentStudent },
  //{ path: 'student/course/applicazioni-internet/assignments', component: AssignmentsContComponentStudent }
];
*/

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  //{ path: 'login', component: LoginDialogComponent },
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'page-not-found', component: PageNotFoundComponent },
  { path: 'register_success', component: RegisterSuccessComponent },
  {
      path: 'student',
      component: HomeComponent,
      children: [
          {
              path: 'course/applicazioni-internet/assignments',
              component: AssignmentsContComponentStudent,
              canActivate: [AuthGuard]
          },            
          {
              path: 'course/applicazioni-internet/teams',
              component: TeamsContComponent,
              canActivate: [AuthGuard]
          },
          {
              path: 'course/applicazioni-internet/vms',
              component: VmsContComponentStudent,
              canActivate: [AuthGuard]
          },
         
      ]
  },

  {
    path: 'teacher',
    component: HomeComponent,
    children: [
        {
            path: 'course/applicazioni-internet/assignments',
            component: AssignmentsContComponentTeacher,
            canActivate: [AuthGuard]
        },            
        {
            path: 'course/applicazioni-internet/students',
            component: StudentsContComponent,
            canActivate: [AuthGuard]
        },
        {
            path: 'course/applicazioni-internet/vms',
            component: VmsContComponentTeacher,
            canActivate: [AuthGuard]
        }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { enableTracing: false } )],
  exports: [RouterModule]
})

export class AppRoutingModule { }
