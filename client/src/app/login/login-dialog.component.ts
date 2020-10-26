import { Component, OnInit, Inject, Injectable, Output, EventEmitter } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatDialogConfig } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { RegisterDialogComponent } from '../register/register-dialog.component';
import { FormControl, Validators, FormGroup, FormBuilder } from '@angular/forms';
import {ForgotPasswordComponent} from '../forgot-password/forgot-password.component'
import { RegisterContComponent } from '../register/register-cont/register-cont.component';
import { ForgotPasswordContComponent } from '../forgot-password/forgot-password-cont/forgot-password-cont.component';

@Component({
  selector: 'app-login-dialog',
  templateUrl: './login-dialog.component.html',
  styleUrls: ['./login-dialog.component.css']
})
export class LoginDialogComponent implements OnInit {

LoginForm: FormGroup;
error: boolean = false;

@Output('login') log = new EventEmitter<any>();

  constructor(
      public matDialog: MatDialog,
      public authService: AuthService,
      private dialogRef: MatDialogRef<LoginDialogComponent>,
      private router: Router,
      private formBuilder: FormBuilder) {

      authService.userLogged.subscribe(ok => {
        if (ok && authService.isLoggedIn()) {


          if (router.url == "/")
            router.navigateByUrl("home");
        }
        else {
          window.alert("Email o password Errata/i");
          this.error = true;
        }
      });

      this.LoginForm = this.formBuilder.group({
        email: new FormControl('',[Validators.email,this.emailDomainValidator]),
        password: ['', [Validators.required, Validators.minLength(8)]],
      });

  }

  ngOnInit() {
  }

  close() {
      this.dialogRef.close();
  }

  login(email: string, password:string) {
    if (!this.LoginForm.valid) {
      window.alert("Controllare che i dati inseriti siano validi e riprovare");
    }
    else {
      this.log.emit({email: email, password: password});
    }
  }

  openDialogRegister() {
    this.dialogRef.close();

    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        id: 1,
        title: 'Register'
    };

    this.matDialog.open(RegisterContComponent, dialogConfig);
  }

  openDialogForgotPassword() {
    this.dialogRef.close();

    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        id: 1,
        title: 'ForgotPwd'
    };

    this.matDialog.open(ForgotPasswordContComponent, dialogConfig);
  }

  emailDomainValidator(control: FormControl) {
    let email = control.value;
    if (email && email.indexOf("@") != -1) {
      let [_, domain] = email.split("@");
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

}
