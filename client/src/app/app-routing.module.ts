import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { StudentsContComponent } from './teacher/students/students-cont.component';

import { HomeComponent } from './home/home.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';

import { AssignmentsContComponent as AssignmentsContComponentTeacher } from './teacher/assignments/assignments-cont.component';
import { AssignmentsContComponent as AssignmentsContComponentStudent } from './student/assignments/assignments-cont/assignments-cont.component';

import { VmsContComponent as VmsContComponentTeacher } from './teacher/vms/vms-cont.component';
import { VmsContComponent as VmsContComponentStudent } from './student/vms/vms-cont/vms-cont.component';

import { HomeworksContComponent as HomeworksContComponentTeacher } from './teacher/assignments/homeworks-cont.component';
import { HomeworksComponent as HomeworksComponentTeacher } from './teacher/assignments/homeworks.component';
import { VersionsContComponent as VersionsContComponentTeacher } from './teacher/assignments/versions-cont.component';
import { VersionsComponent as VersionsComponentTeacher } from './teacher/assignments/versions.component';

import { TeamsContComponent } from './student/teams/teams-cont/teams-cont.component';
import { RegisterSuccessComponent } from './register-success/register-success.component';
import { AuthGuard} from './auth/auth.guard';
import {LoginDialogComponent} from './login/login-dialog.component';
import { UserNotAllowedComponent } from './not-allowed/user-not-allowed.component';


const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginDialogComponent },
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'page-not-found', component: PageNotFoundComponent },
  { path: 'register-success', component: RegisterSuccessComponent },
  { path: ':role/course/:courses/:tab/not-allowed', component: UserNotAllowedComponent },
  {
      path: 'student',
      children: [
          {
              path: 'course/:courses/assignments',
              component: AssignmentsContComponentStudent,
              canActivate: [AuthGuard]
          },            
          {
              path: 'course/:courses/teams',
              component: TeamsContComponent,
              canActivate: [AuthGuard]
          },
          {
              path: 'course/:courses/vms',
              component: VmsContComponentStudent,
              canActivate: [AuthGuard]
          },
         
      ]
  },

  {
    path: 'teacher',
    children: [
        {
            path: 'course/:courses/assignments',
            component: AssignmentsContComponentTeacher,
            canActivate: [AuthGuard],
            children: [
              { path: ':id/homeworks', component: HomeworksContComponentTeacher,
                children: [
                  { path: ':id/versions', component: VersionsContComponentTeacher }
                  ],
              }
            ],  
        },            
        {
            path: 'course/:courses/students',
            component: StudentsContComponent,
            canActivate: [AuthGuard]
        },
        {
            path: 'course/:courses/vms',
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
