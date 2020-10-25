import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/auth/auth.service';
import { Md5 } from 'ts-md5/dist/md5';

@Component({
  selector: 'app-login-cont',
  templateUrl: './login-cont.component.html',
  styleUrls: ['./login-cont.component.css']
})
export class LoginContComponent implements OnInit {

  private md5: Md5;

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
  }

  login(content: any) {
    let email: string = content.email;
    let password: string = content.password;

    this.md5 = new Md5();

    this.authService.login(email, this.md5.start().appendStr(password).end().toString());
    
  }

}
