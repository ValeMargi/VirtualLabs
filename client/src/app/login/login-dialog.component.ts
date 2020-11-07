import { Component, OnInit, Inject, Injectable, Output, EventEmitter, Input, OnChanges, SimpleChanges } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatDialogConfig } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { RegisterDialogComponent } from '../register/register-dialog.component';
import { FormControl, Validators, FormGroup, FormBuilder } from '@angular/forms';
import {ForgotPasswordComponent} from '../forgot-password/forgot-password.component'
import { RegisterContComponent } from '../register/register-cont/register-cont.component';

import { ForgotPasswordContComponent } from '../forgot-password/forgot-password-cont/forgot-password-cont.component';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-login-dialog',
  templateUrl: './login-dialog.component.html',
  styleUrls: ['./login-dialog.component.css']
})
export class LoginDialogComponent implements OnInit, OnChanges {

LoginForm: FormGroup;

@Input() badCredentials: boolean;
@Output('login') log = new EventEmitter<any>();

  constructor(
      public matDialog: MatDialog,
      public authService: AuthService,
      private dialogRef: MatDialogRef<LoginDialogComponent>,
      private formBuilder: FormBuilder,
      private router: Router) {

      this.LoginForm = this.formBuilder.group({
        email: new FormControl('',[Validators.email, this.emailDomainValidator]),
        password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(20)]],
      });

  }

  ngOnInit() {

  }

  ngOnChanges(changes: SimpleChanges) {
    this.badCredentials = changes.badCredentials.currentValue;
  }

  close() {
    this.dialogRef.close();
  }


  emailDomainValidator(control: FormControl) {
    let email = control.value;
    if (email && email.indexOf("@") != -1) {
      let [_, domain] = email.toLowerCase().split("@");
      if (domain !== "studenti.polito.it" && domain !== "polito.it") {
        return {
          emailDomain: {
            parsedDomain: domain
          }
        }
      }
    }
    return null;

  }

  login(email: string, password:string) {
    if (!this.LoginForm.valid) {
      this.badCredentials = true;
      return;
    }
    else {
      this.log.emit({email: email.toLowerCase(), password: password});
    }
  }

  routeToRegister() {
    this.close();
    this.router.navigate([], {queryParams: {doRegister : "true"}}); //la route viene triggerata nell'app component
  }

  routeForgotPass() {
    this.close();
    this.router.navigate([], {queryParams: {forgotPass : "true"}});
  }

  ngOnDestroy() {
  }


}
