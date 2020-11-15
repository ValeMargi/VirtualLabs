import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { AuthService } from 'src/app/auth/auth.service';
import { RegisterDialogComponent } from '../register-dialog.component';

@Component({
  selector: 'app-register-cont',
  templateUrl: './register-cont.component.html',
  styleUrls: ['./register-cont.component.css']
})
export class RegisterContComponent implements OnInit {

  QUERYING: boolean = false;
  OK: boolean = false;

  constructor(private authService: AuthService,
              private dialogRef: MatDialogRef<RegisterDialogComponent>) { }

  ngOnInit(): void {
  }

  register(content: any) {
    let image = content.image;
    let userJson = content.userJson;
    this.QUERYING = true; //si mostra una progress bar
    this.OK = false;

    this.authService.registerUser(image, userJson).subscribe(
      (data) => {
        this.QUERYING = false;  //si toglie la progress bar...
        this.OK = true; //e si mostra un messaggio di avvenuta registrazione
        setTimeout(()=>{this.close()}, 3000); //se va a buon fine, si chiude la dialog dopo 3 secondi

      },
      (error: any) => {
        window.alert(error.error.message);
        this.QUERYING = false;
      }
    );
  }

  close() {
    this.dialogRef.close();
  }


}
