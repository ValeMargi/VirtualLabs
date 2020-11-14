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
import { MatProgressBarModule } from '@angular/material/progress-bar';
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
import { MatSelectModule } from '@angular/material/select';
import { ManageModelComponent } from './teacher/vms/manage-model.component';
import { ManageModelContComponent } from './teacher/vms/manage-model-cont.component';
import { EditProfileContComponent } from './edit-profile/edit-profile-cont.component';
import { EditProfileComponent } from './edit-profile/edit-profile.component';
import { CreateAssignmentContComponent } from './teacher/assignments/create-assignment/create-assignment-cont.component';
import { CreateAssignmentComponent } from './teacher/assignments/create-assignment/create-assignment.component';
import { RequestTeamDialogContComponent } from './student/teams/request-team-dialog/request-team-dialog-cont/request-team-dialog-cont.component';
import { AddHomeworkComponent } from './student/assignments/add-homework/add-homework.component';
import { AddHomeworkContComponent } from './student/assignments/add-homework/add-homework-cont/add-homework-cont.component';
import { HomeworksContComponent as HomeworksContComponentTeacher } from './teacher/assignments/homeworks-cont.component';
import { HomeworksComponent as HomeworksComponentTeacher } from './teacher/assignments/homeworks.component';
import { VersionsContComponent as VersionsContComponentTeacher } from './teacher/assignments/versions-cont.component';
import { VersionsComponent as VersionsComponentTeacher } from './teacher/assignments/versions.component';
import { TeamVmsContComponent } from './teacher/vms/team-vms-cont.component';
import { TeamVmsComponent } from './teacher/vms/team-vms.component';
import { CreateVmsComponent } from './student/vms/create-vms/create-vms.component';
import { CreateVmsContComponent } from './student/vms/create-vms/create-vms-cont/create-vms-cont.component';
import { AddCourseContComponent } from './teacher/add-course/add-course-cont.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { FooterComponent } from './footer/footer.component';
import { VersionsComponent as VersionsComponentStudent } from './student/assignments/versions/versions.component';
import { VersionsContComponent as VersionsContComponentStudent } from './student/assignments/versions/versions-cont/versions-cont.component';
import { RegisterSuccessComponent } from './register-success/register-success.component';
import { ManageVmComponent } from './student/vms/manage-vm/manage-vm.component';
import { ManageVmContComponent } from './student/vms/manage-vm/manage-vm-cont/manage-vm-cont.component';
import { ViewImageContComponent } from './view-image/view-image-cont/view-image-cont.component';
import { ViewImageComponent } from './view-image/view-image.component';
import { RegisterContComponent } from './register/register-cont/register-cont.component';
import { LoginContComponent } from './login/login-cont/login-cont.component';

import {MatMomentDateModule} from '@angular/material-moment-adapter';
import {MatDatepickerModule} from '@angular/material/datepicker';

import {MatExpansionModule} from '@angular/material/expansion';
import { ForgotPasswordContComponent } from './forgot-password/forgot-password-cont/forgot-password-cont.component';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { ChangePasswordContComponent } from './change-password/change-password-cont/change-password-cont.component';
import { UploadCorrectionContComponent } from './teacher/assignments/upload-correction/upload-correction-cont/upload-correction-cont.component';
import { UploadCorrectionComponent } from './teacher/assignments/upload-correction/upload-correction.component';
import { EditCourseContComponent } from './teacher/edit-course/edit-course-cont/edit-course-cont.component';
import { EditCourseComponent } from './teacher/edit-course/edit-course.component';

import { CommonModule } from '@angular/common';
import { MAT_DATE_FORMATS } from '@angular/material/core';

export const MY_FORMATS = {
  parse: {
      dateInput: 'LL'
  },
  display: {
      dateInput: 'YYYY-MM-DD',
      monthYearLabel: 'YYYY',
      dateA11yLabel: 'LL',
      monthYearA11yLabel: 'YYYY'
  }
};

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
    HomeworksContComponentTeacher,
    HomeworksComponentTeacher,
    VersionsContComponentTeacher,
    VersionsComponentTeacher,
    TeamVmsContComponent,
    TeamVmsComponent,
    CreateVmsComponent,
    CreateVmsContComponent,
    AddCourseContComponent,
    ForgotPasswordComponent,
    FooterComponent,
    VersionsComponentStudent,
    VersionsContComponentStudent,
    RegisterSuccessComponent,
    ManageVmComponent,
    ManageVmContComponent,
    ViewImageContComponent,
    ViewImageComponent,
    RegisterContComponent,
    LoginContComponent,
    ForgotPasswordContComponent,
    ChangePasswordComponent,
    ChangePasswordContComponent,
    UploadCorrectionContComponent,
    UploadCorrectionComponent,
    EditCourseContComponent,
    EditCourseComponent
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
    MatProgressBarModule,
    TextFieldModule,
    MatExpansionModule,
    MatDatepickerModule,
    MatMomentDateModule,
    CommonModule
  ],
  entryComponents: [MatDialogModule, MatFormFieldModule],
  providers: [{ provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
              { provide: MAT_DATE_FORMATS, useValue: MY_FORMATS }],
  bootstrap: [AppComponent]
})

export class AppModule {

}
