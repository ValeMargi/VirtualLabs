import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { StudentsContComponent } from './teacher/students-cont.component';
import { VmsContComponent } from './teacher/vms-cont.component';
import { HomeComponent } from './home.component';
import { PageNotFoundComponent } from './page-not-found.component';


const routes: Routes = [
  { path: '', component: PageNotFoundComponent },
  { path: 'home', component: HomeComponent },
  { path: 'teacher/course/applicationi-internet/students', component: StudentsContComponent },
  { path: 'teacher/course/applicationi-internet/vms', component: VmsContComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { enableTracing: false } )],
  exports: [RouterModule]
})

export class AppRoutingModule { }
