import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/auth/auth.service';

@Component({
  selector: 'app-forgot-password-cont',
  templateUrl: './forgot-password-cont.component.html',
  styleUrls: ['./forgot-password-cont.component.css']
})
export class ForgotPasswordContComponent implements OnInit {
  OK: boolean = false;
  ERROR: boolean = false;
  QUERYING: boolean = false;

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
  }

  resetPassword(email: string) {
    this.QUERYING = true; //si mostra una progress bar

    this.authService.resetPassword(email).subscribe(
      (data) => {
        //in base all'esito, si mostra un messaggio di errore o di conferma
        if (data == true) {
          this.OK = true;
          this.ERROR = false;
        }
        else {
          this.OK = false;
          this.ERROR = true;
        }

        this.QUERYING = false;
      },
      (error) => {
        window.alert(error.error.message);
        
        this.OK = false;
        this.ERROR = true;
        this.QUERYING = false;
      }
    );
  }

}
