import { Subscription } from 'rxjs/internal/Subscription';
import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/auth/auth.service';
import { Md5 } from 'ts-md5/dist/md5';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-login-cont',
  templateUrl: './login-cont.component.html',
  styleUrls: ['./login-cont.component.css']
})
export class LoginContComponent implements OnInit {

  private md5: Md5;
  BAD_CREDENTIALS: boolean = false;

  constructor(private authService: AuthService,
              private dialogRef: MatDialogRef<LoginContComponent>) {

               }

  ngOnInit(): void {
    
  }

  close() {
    this.dialogRef.close();
  }

  login(content: any) {
    let email: string = content.email;
    let password: string = content.password;

    this.md5 = new Md5();

    this.BAD_CREDENTIALS = false;

    this.authService.login(email, this.md5.start().appendStr(password).end().toString()).subscribe(
      (data) => {
        this.authService.setSession(data);
        this.close();
      },
      (error) => {
        this.authService.userLogged.emit(false);

        if (error.error.status == 401) {
          this.BAD_CREDENTIALS = true;
        }
        else {
          window.alert(error.error.message);
        }
      }
    );
  }

}
