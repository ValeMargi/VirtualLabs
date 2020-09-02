import { BrowserModule } from '@angular/platform-browser';
import { NgModule, Component, ElementRef, ViewChild } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TextFieldModule } from '@angular/cdk/text-field';
import { StudentsComponent } from './teacher/students/students.component';
import { StudentsContComponent } from './teacher/students/students-cont.component';
import { HomeComponent } from './home/home.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { AuthInterceptor } from './auth/auth.interceptor';
import { MatCardModule } from '@angular/material/card';
import { LoginDialogComponent } from './login/login-dialog.component';
import { RegisterDialogComponent } from './register/register-dialog.component';
import { AddCourseDialogComponent } from './teacher/add-course/add-course-dialog.component';
import { VmsComponent as VmsComponentTeacher } from './teacher/vms/vms.component';
import { VmsContComponent as VmsContComponentTeacher } from './teacher/vms/vms-cont.component';
import { VmsComponent as VmsComponentStudent } from './student/vms/vms.component';
import { VmsContComponent as VmsContComponentStudent } from './student/vms/vms-cont/vms-cont.component';
import { AssignmentsComponent as AssignmentsComponentTeacher } from './teacher/assignments/assignments.component';
import { AssignmentsContComponent as AssignmentsContComponentTeacher } from './teacher/assignments/assignments-cont.component';
import { AssignmentsComponent as AssignmentsComponentStudent } from './student/assignments/assignments.component';
import { AssignmentsContComponent as AssignmentsContComponentStudent } from './student/assignments/assignments-cont/assignments-cont.component';
import { TeamsContComponent } from './student/teams/teams-cont/teams-cont.component';
import { TeamsComponent } from './student/teams/teams.component';
import { RequestTeamDialogComponent } from './student/teams/request-team-dialog/request-team-dialog.component';
import {MatSelectModule} from '@angular/material/select';
import { ManageModelComponent } from './teacher/vms/manage-model.component';
import { ManageModelContComponent } from './teacher/vms/manage-model-cont.component';
import { EditProfileContComponent } from './edit-profile/edit-profile-cont.component';
import { EditProfileComponent } from './edit-profile/edit-profile.component';
import { CreateAssignmentContComponent } from './teacher/assignments/create-assignment/create-assignment-cont.component';
import { CreateAssignmentComponent } from './teacher/assignments/create-assignment/create-assignment.component';
import { RequestTeamDialogContComponent } from './student/teams/request-team-dialog/request-team-dialog-cont/request-team-dialog-cont.component';
import { AddHomeworkComponent } from './student/assignments/add-homework/add-homework.component';
import { AddHomeworkContComponent } from './student/assignments/add-homework/add-homework-cont/add-homework-cont.component';
import { HomeworksContComponent } from './teacher/assignments/homeworks-cont.component';
import { HomeworksComponent } from './teacher/assignments/homeworks.component';
import { VersionsContComponent } from './teacher/assignments/versions-cont.component';
import { VersionsComponent } from './teacher/assignments/versions.component';
import { TeamVmsContComponent } from './teacher/vms/team-vms-cont.component';
import { TeamVmsComponent } from './teacher/vms/team-vms.component';
import { CreateVmsComponent } from './student/vms/create-vms/create-vms.component';
import { CreateVmsContComponent } from './student/vms/create-vms/create-vms-cont/create-vms-cont.component';
import { AddCourseContComponent } from './teacher/add-course/add-course-cont.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { FooterComponent } from './footer/footer.component';

@NgModule({
  declarations: [
    AppComponent,
    StudentsComponent,
    StudentsContComponent,
    HomeComponent,
    PageNotFoundComponent,
    LoginDialogComponent,
    RegisterDialogComponent,
    AddCourseDialogComponent,
    VmsComponentTeacher,
    VmsContComponentTeacher,
    VmsComponentStudent,
    VmsContComponentStudent,
    AssignmentsComponentTeacher,
    AssignmentsContComponentTeacher,
    AssignmentsComponentStudent, 
    AssignmentsContComponentStudent,
    TeamsContComponent,
    TeamsComponent,
    RequestTeamDialogComponent,
    ManageModelComponent,
    ManageModelContComponent,
    EditProfileContComponent,
    EditProfileComponent,
    CreateAssignmentContComponent,
    CreateAssignmentComponent,
    RequestTeamDialogContComponent,
    AddHomeworkComponent,
    AddHomeworkContComponent,
    HomeworksContComponent,
    HomeworksComponent,
    VersionsContComponent,
    VersionsComponent,
    TeamVmsContComponent,
    TeamVmsComponent,
    CreateVmsComponent,
    CreateVmsContComponent,
    AddCourseContComponent,
    ForgotPasswordComponent,
    FooterComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatTabsModule,
    MatSortModule,
    MatPaginatorModule,
    MatIconModule,
    MatTableModule,
    MatCheckboxModule,
    MatInputModule,
    MatAutocompleteModule,
    MatButtonModule,
    MatSelectModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatDialogModule,
    MatFormFieldModule,
    MatCardModule, 
    TextFieldModule,
  ],
  entryComponents: [MatDialogModule, MatFormFieldModule],
  providers: [{ provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }],
  bootstrap: [AppComponent]
})

export class AppModule { 

}
