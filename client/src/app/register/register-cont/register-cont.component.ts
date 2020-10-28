import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { AuthService } from 'src/app/auth/auth.service';

@Component({
  selector: 'app-register-cont',
  templateUrl: './register-cont.component.html',
  styleUrls: ['./register-cont.component.css']
})
export class RegisterContComponent implements OnInit {

  QUERYING: boolean = false;
  OK: boolean = false;

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
  }

  register(content: any) {
    let image = content.image;
    let userJson = content.userJson;
    this.QUERYING = true;
    this.OK = false;

    this.authService.registerUser(image, userJson).subscribe(
      (data) => {
        this.QUERYING = false;
        this.OK = true;
      },
      (error: any) => {
        window.alert(error.error.message);
        this.QUERYING = false;
      }
    );
  }

}
