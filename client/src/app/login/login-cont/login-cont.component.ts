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
  private route$: Subscription

  constructor(private authService: AuthService,
              private dialogRef: MatDialogRef<LoginContComponent>,
              private router: Router,
              private route: ActivatedRoute) {

               }

  ngOnInit(): void {
    this.authService.userLogged.subscribe(ok => {
      if (ok && this.authService.isLoggedIn()) {
        this.close();
      }
      else {
        //this.error = true;
      }
    });
  }

  close() {
    this.dialogRef.close();
  }

  login(content: any) {
    let email: string = content.email;
    let password: string = content.password;

    this.md5 = new Md5();

    this.authService.login(email, this.md5.start().appendStr(password).end().toString());


  }

}
