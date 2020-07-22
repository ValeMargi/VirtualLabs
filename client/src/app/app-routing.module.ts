import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { StudentsContComponent } from './teacher/students-cont.component';
import { VmsContComponent as VmsContComponentTeacher } from './teacher/vms-cont.component';
import { VmsContComponent as VmsContComponentStudent } from './student/vms-cont.component';
import { HomeComponent } from './home.component';
import { PageNotFoundComponent } from './page-not-found.component';
import { AssignmentsContComponent as AssignmentsContComponentTeacher } from './teacher/assignments-cont.component';
import { AssignmentsContComponent as AssignmentsContComponentStudent } from './student/assignments-cont.component';
import { TeamsContComponent } from './student/teams-cont.component';


const routes: Routes = [
  { path: '', component: PageNotFoundComponent },
  { path: 'home', component: HomeComponent },
  { path: 'teacher/course/applicazioni-internet/students', component: StudentsContComponent },
  { path: 'teacher/course/applicazioni-internet/vms', component: VmsContComponentTeacher },
  { path: 'teacher/course/applicazioni-internet/assignments', component: AssignmentsContComponentTeacher },
  { path: 'student/course/applicazioni-internet/teams', component: TeamsContComponent },
  { path: 'student/course/applicazioni-internet/vms', component: VmsContComponentStudent },
  { path: 'student/course/applicazioni-internet/assignments', component: AssignmentsContComponentStudent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { enableTracing: false } )],
  exports: [RouterModule]
})

export class AppRoutingModule { }