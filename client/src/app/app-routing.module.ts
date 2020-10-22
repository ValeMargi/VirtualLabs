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
import { UserNotAllowedComponent } from './not-allowed/user-not-allowed.component';
import { TeamPartecipationComponent } from './team-creation/team-partecipation.component';
import { TeamVmsContComponent } from './teacher/vms/team-vms-cont.component';
import { ChangePasswordContComponent } from './change-password/change-password-cont/change-password-cont.component';


const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'home?doLogin=true', component: HomeComponent },
  { path: 'page-not-found', component: PageNotFoundComponent },
  { path: 'register/:success/:token', component: RegisterSuccessComponent },
  { path: 'team/:success/:token', component: TeamPartecipationComponent },
  { path: ':role/course/:courses/:tab/not-allowed', component: UserNotAllowedComponent },
  { path: 'user/password-reset', component: ChangePasswordContComponent },
  {
      path: 'student',
      children: [
          {
            path: 'course',
            canActivate: [AuthGuard],
            children: [
            {
              path: ':courses/assignments',
              component: AssignmentsContComponentStudent,
              children: [
                { path: ':idV/versions', component: VersionsContComponentStudent
                }
              ],
            },
            {
              path: ':courses/teams',
              component: TeamsContComponent,
            },
            {
              path: ':courses/vms',
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
          canActivate: [AuthGuard],
          children: [
            {
              path: ':courses/assignments',
              component: AssignmentsContComponentTeacher,
              children: [
                { path: ':idA/homeworks', component: HomeworksContComponentTeacher,
                  children: [
                    { path: ':idH/versions', component: VersionsContComponentTeacher }
                    ],
                }
              ],
            },
            {
              path: ':courses/students',
              component: StudentsContComponent,
            },
            {
              path: ':courses/vms',
              component: VmsContComponentTeacher,
              children: [
                { path: 'team/:idT', component: TeamVmsContComponent }
              ]
            }
          ]
        }
    ]
  },
  { path: '', redirectTo: 'home', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { enableTracing: false } )],
  exports: [RouterModule]
})

export class AppRoutingModule { }
