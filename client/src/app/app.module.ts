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
import { StudentsComponent } from './teacher/students.component';
import { StudentsContComponent } from './teacher/students-cont.component';
import { HomeComponent } from './home.component';
import { VmsContComponent } from './teacher/vms-cont.component';
import { PageNotFoundComponent } from './page-not-found.component';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { AuthInterceptor } from './services/auth.interceptor';
import { StudentInterceptor } from './services/students.interceptor';
import { MatCardModule } from '@angular/material/card';
import { LoginDialogComponent } from './login-dialog.component';
import { RegisterDialogComponent } from './register-dialog.component';
import { AddCourseDialogComponent } from './teacher/add-course-dialog.component';

@NgModule({
  declarations: [
    AppComponent,
    StudentsComponent,
    StudentsContComponent,
    HomeComponent,
    VmsContComponent,
    PageNotFoundComponent,
    LoginDialogComponent,
    RegisterDialogComponent,
    AddCourseDialogComponent
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
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatDialogModule,
    MatFormFieldModule,
    MatCardModule
  ],
  entryComponents: [MatDialogModule, MatFormFieldModule],
  providers: [{ provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }/*,
  { provide: HTTP_INTERCEPTORS, useClass: StudentInterceptor, multi: true }*/],
  bootstrap: [AppComponent]
})

export class AppModule { 

}
