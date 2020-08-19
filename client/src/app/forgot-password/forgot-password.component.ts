import { Component, OnInit, Inject, Injectable } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatDialogConfig } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { RegisterDialogComponent } from '../register/register-dialog.component';
import { FormControl, Validators, FormGroup, FormBuilder } from '@angular/forms';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit {

  ForgotPasswordForm: FormGroup;

 constructor(
      public matDialog: MatDialog, 
      public authService: AuthService,
      private dialogRef: MatDialogRef<ForgotPasswordComponent>,
      private router: Router,
      private formBuilder: FormBuilder) {

      this.ForgotPasswordForm = this.formBuilder.group({
        email: new FormControl('',[Validators.email,this.emailDomainValidator])
      });
  
  }

  ngOnInit() {
  }

  close() {
      this.dialogRef.close();
  }

  sendPassword(email) {
    if (email.value.toString().length == 0) {
      document.getElementById("error").style.visibility = "visible";
    }
    else {
      //this.authService.sendPassword(email.value.toString())
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
