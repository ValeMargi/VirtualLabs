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

import { VersionsContComponent as VersionsContComponentStudent } from './student/assignments/versions/versions-cont/versions-cont.component';
import { VersionsComponent as VersionsComponentStudent } from './student/assignments/versions/versions.component';

import { TeamsContComponent } from './student/teams/teams-cont/teams-cont.component';
import { RegisterSuccessComponent } from './register-success/register-success.component';
import { AuthGuard} from './auth/auth.guard';
import {LoginDialogComponent} from './login/login-dialog.component';
import { TeamVmsContComponent } from './teacher/vms/team-vms-cont.component';
import { ChangePasswordContComponent } from './change-password/change-password-cont/change-password-cont.component';
import { RequestTeamDialogContComponent } from './student/teams/request-team-dialog/request-team-dialog-cont/request-team-dialog-cont.component';


const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'page-not-found', component: PageNotFoundComponent },
  { path: 'register/confirmation', component: RegisterSuccessComponent },
  { path: 'user/password-reset', component: ChangePasswordContComponent },
  {
      path: 'student',
      children: [
          {
            path: 'course',
            children: [
            {
              path: ':courses/assignments',
              canActivate: [AuthGuard],
              component: AssignmentsContComponentStudent,
              children: [
                { path: ':idA/versions', 
                  canActivate: [AuthGuard],
                  component: VersionsContComponentStudent
                }
              ],
            },
            {
              path: ':courses/teams',
              canActivate: [AuthGuard],
              component: TeamsContComponent,
              children: [
                { path: 'request',
                  canActivate: [AuthGuard],
                  component: RequestTeamDialogContComponent
                }
              ],
            },
            {
              path: ':courses/vms',
              canActivate: [AuthGuard],
              component: VmsContComponentStudent,
            }
          ]
        }
      ]
  },

  {
    path: 'teacher',
    children: [
        {
          path: 'course',
          children: [
            {
              path: ':courses/assignments',
              canActivate: [AuthGuard],
              component: AssignmentsContComponentTeacher,
              children: [
                { path: ':idA/homeworks', component: HomeworksContComponentTeacher,
                  canActivate: [AuthGuard],
                  children: [
                    { path: ':idH/versions', component: VersionsContComponentTeacher,
                      canActivate: [AuthGuard] }
                    ],
                }
              ],
            },
            {
              path: ':courses/students',
              canActivate: [AuthGuard],
              component: StudentsContComponent,
            },
            {
              path: ':courses/vms',
              canActivate: [AuthGuard],
              component: VmsContComponentTeacher,
              children: [
                { path: 'team/:idT', component: TeamVmsContComponent }
              ]
            }
          ]
        }
    ]
  },
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: '**', redirectTo: 'page-not-found', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { enableTracing: false } )],
  exports: [RouterModule]
})

export class AppRoutingModule { }
