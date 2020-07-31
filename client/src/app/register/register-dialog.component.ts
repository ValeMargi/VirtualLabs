import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef, MatDialogConfig } from '@angular/material/dialog';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { LoginDialogComponent } from '../login/login-dialog.component';

@Component({
  selector: 'app-register-dialog',
  templateUrl: './register-dialog.component.html',
  styleUrls: ['./register-dialog.component.css']
})
export class RegisterDialogComponent implements OnInit {

  constructor(
    public matDialog: MatDialog, 
    public authService: AuthService,
    private dialogRef: MatDialogRef<RegisterDialogComponent>,
    private router: Router) {

    authService.userLogged.subscribe(ok => {
      if (ok && authService.isLoggedIn()) {
        
        if (router.url == "/")
          router.navigateByUrl("home");
      }
      else {
        document.getElementById("error").style.visibility = "visible";
      }
    });
}

ngOnInit() {
}

close() {
    this.dialogRef.close();
}

openDialogLogin() {
  this.dialogRef.close();
  
  const dialogConfig = new MatDialogConfig();

  dialogConfig.disableClose = false;
  dialogConfig.autoFocus = true;

  dialogConfig.data = {
      id: 1,
      title: 'Login'
  };

  this.matDialog.open(LoginDialogComponent, dialogConfig);
}

register() {

}

}
