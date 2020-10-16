import { Component, OnInit, Inject, Injectable, Output, EventEmitter, Input, OnChanges, SimpleChanges } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatDialogConfig } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { RegisterDialogComponent } from '../register/register-dialog.component';
import { FormControl, Validators, FormGroup, FormBuilder } from '@angular/forms';
import { RegisterContComponent } from '../register/register-cont/register-cont.component';
import { emit } from 'process';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit, OnChanges {

  ForgotPasswordForm: FormGroup;

  @Input() ok: boolean;
  @Input() error: boolean;
  @Input() querying: boolean;
  @Output('reset') reset = new EventEmitter<string>();

 constructor(
      public matDialog: MatDialog, 
      public authService: AuthService,
      private dialogRef: MatDialogRef<ForgotPasswordComponent>,
      private router: Router,
      private formBuilder: FormBuilder) {

      this.ForgotPasswordForm = this.formBuilder.group({
        id: new FormControl('',[Validators.required])
      });
  
  }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.ok != null) {
      this.ok = changes.ok.currentValue;
    }

    if (changes.error != null) {
      this.error = changes.error.currentValue;
    }
  }

  close() {
    this.dialogRef.close();
  }

  resetPassword(email) {
    this.reset.emit(email);
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

}
