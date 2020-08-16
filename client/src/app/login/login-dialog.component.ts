import { Component, OnInit, Inject, Injectable } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatDialogConfig } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { RegisterDialogComponent } from '../register/register-dialog.component';
import { FormControl, Validators, FormGroup, FormBuilder } from '@angular/forms';

@Component({
  selector: 'app-login-dialog',
  templateUrl: './login-dialog.component.html',
  styleUrls: ['./login-dialog.component.css']
})
export class LoginDialogComponent implements OnInit {
  
LoginForm: FormGroup;

  constructor(
      public matDialog: MatDialog, 
      public authService: AuthService,
      private dialogRef: MatDialogRef<LoginDialogComponent>,
      private router: Router,
      private formBuilder: FormBuilder) {

      authService.userLogged.subscribe(ok => {
        if (ok && authService.isLoggedIn()) {
          this.dialogRef.close();
          
          if (router.url == "/")
            router.navigateByUrl("home");
        }
        else {
          document.getElementById("error").style.visibility = "visible";
        }
      });

      this.LoginForm = this.formBuilder.group({
        email: new FormControl('',[Validators.email,this.emailDomainValidator]),
        password: ['', [Validators.required,Validators.minLength(8)]],
      });
  
  }

  ngOnInit() {
  }

  close() {
      this.dialogRef.close();
  }

  login(email, password) {
    if (email.value.toString().length == 0 || password.value.toString().length == 0) {
      document.getElementById("error").style.visibility = "visible";
    }
    else {
      this.authService.login(email.value.toString(), password.value.toString())
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

    this.matDialog.open(RegisterDialogComponent, dialogConfig);
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
