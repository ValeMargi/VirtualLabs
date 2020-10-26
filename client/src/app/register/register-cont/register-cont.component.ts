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

  constructor(private authService: AuthService, private matDialogRef: MatDialogRef<RegisterContComponent>) { }

  ngOnInit(): void {
  }

  register(content: any) {
    let image = content.image;
    let userJson = content.userJson;
    this.QUERYING = true;

    this.authService.registerUser(image, userJson).subscribe(
      (data) => {
        this.matDialogRef.close();
        this.QUERYING = false;
        window.alert("Richiesta di registrazione avvenuta con successo. Controlla la tua posta per attivare l'account")
      },
      (error: any) => {
        this.QUERYING = false;
      }
    );
  }

}
