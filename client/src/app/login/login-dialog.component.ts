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
route$: Subscription;
routeQueryParams$: Subscription;


@Input() badCredentials: boolean;
@Output('login') log = new EventEmitter<any>();

  constructor(
      public matDialog: MatDialog,
      public authService: AuthService,
      private dialogRef: MatDialogRef<LoginDialogComponent>,
      private formBuilder: FormBuilder,
      private router: Router,
      private route: ActivatedRoute) {

      this.LoginForm = this.formBuilder.group({
        email: new FormControl('',[Validators.email,this.emailDomainValidator]),
        password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(20)]],
      });

  }

  ngOnInit() {

    this.routeQueryParams$ = this.route.queryParams.subscribe(params => {
      if (params['forgotPass']) {
        if (this.authService.isLoggedOut()) {
          this.openDialogForgotPassword();
        }
      }
        if (params['toRegister']) {
        if (this.authService.isLoggedOut()) {
          this.openDialogRegister();
        }
        }
  });

  }

  ngOnChanges(changes: SimpleChanges) {
    this.badCredentials = changes.badCredentials.currentValue;
  }

  close() {
    this.dialogRef.close();
  }

  login(email: string, password:string) {
    if (!this.LoginForm.valid) {
      this.badCredentials = true;
      return;
    }
    else {
      this.badCredentials = false;
      this.log.emit({email: email.toLowerCase(), password: password});
    }
  }

  routeToRegister() {
    this.router.navigate([], {queryParams: {toRegister : "true"}});
  }

  openDialogRegister() {
    this.dialogRef.close();

    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '50%';

    dialogConfig.data = {
        id: 1,
        title: 'Register'
    };

    const dialogRef = this.matDialog.open(RegisterContComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(result => {
      const queryParams = {}
      const url = this.authService.getStoredUrl();
      this.authService.storeUrl(null);

      if (url != null && this.authService.isLoggedIn()) {
        this.router.navigateByUrl(url);
      }
      else {
        this.router.navigate([], { queryParams, replaceUrl: true, relativeTo: this.route });
      }
    });

  }

  routeForgotPass() {
    this.router.navigate([], {queryParams: {forgotPass : "true"}});
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

    const dialogRef = this.matDialog.open(ForgotPasswordContComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(result => {
      const queryParams = {}
      const url = this.authService.getStoredUrl();
      this.authService.storeUrl(null);

      if (url != null && this.authService.isLoggedIn()) {
        this.router.navigateByUrl(url);
      }
      else {
        this.router.navigate([], { queryParams, replaceUrl: true, relativeTo: this.route });
      }
    });
  }

  ngOnDestroy() {
    this.route$.unsubscribe();
    this.routeQueryParams$.unsubscribe();
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
