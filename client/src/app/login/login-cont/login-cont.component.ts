import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/auth/auth.service';

@Component({
  selector: 'app-login-cont',
  templateUrl: './login-cont.component.html',
  styleUrls: ['./login-cont.component.css']
})
export class LoginContComponent implements OnInit {

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
  }

  login(content: any) {
    let email = content.email;
    let password = content.password;

    this.authService.login(email.value.toString(), password.value.toString());
  }

}
