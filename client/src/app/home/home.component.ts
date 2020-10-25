import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { Subscription } from 'rxjs/internal/Subscription';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { LoginDialogComponent } from '../login/login-dialog.component';
import { LoginContComponent } from '../login/login-cont/login-cont.component';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy {

  private route$: Subscription;
  askLoginVisibility = true;

  constructor(private matDialog: MatDialog,
              private authService: AuthService,
              private route: ActivatedRoute) { }

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.askLoginVisibility = false;
    }
    else {
      this.askLoginVisibility = true;
    }

    this.authService.userLogged.subscribe(
      (data) => {
        if (data == true) {
          this.askLoginVisibility = false;
          //Alex scrivi qua
        }
        else {
          this.askLoginVisibility = true;
        }
      },
      (error) => {

      }
    );

    this.route$ = this.route.queryParams.subscribe(params => {
      let login = params['doLogin'];

      if (login == 'true') {
        this.openDialogLogin();
      }
    });
  }

  openDialogLogin() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        id: 1,
        title: 'Login'
      
    };

    this.matDialog.open(LoginContComponent, dialogConfig);
  }

  ngOnDestroy() {
    if (this.route$ != null) {
      this.route$.unsubscribe();
    }
  }

}
