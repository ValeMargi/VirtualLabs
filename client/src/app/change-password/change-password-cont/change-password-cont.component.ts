import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';
import { AuthService } from 'src/app/auth/auth.service';

@Component({
  selector: 'app-change-password-cont',
  templateUrl: './change-password-cont.component.html',
  styleUrls: ['./change-password-cont.component.css']
})
export class ChangePasswordContComponent implements OnInit, OnDestroy {

  private route$: Subscription;
  private token: string;

  OK: boolean = false;
  ERROR: boolean = false;
  MSG: string;
  
  constructor(private authService: AuthService,
              private route: ActivatedRoute) { }


  ngOnInit(): void {
    this.route$ = this.route.queryParams.subscribe(params => {
      let token = params['token'];
      let msg = params['error'];

      if (token) {
        this.token = token;
      }
      else if (msg) {
        this.MSG = msg;
      }
    });
  }

  ngOnDestroy(): void {
    this.route$.unsubscribe();
  }

  changePassword(password: string) {
    if (!this.token) {
      window.alert("Impossibile reperire il token, riprovare");
      return;
    }

    this.authService.savePassword(this.token, password).subscribe(
      (data) => {
        if (data) {
          this.OK = true;
          this.ERROR = false;
        }
        else {
          this.OK = false;
          this.ERROR = true;
        }
      },
      (error) => {
        this.OK = false;
        this.ERROR = true;
      }
    );
  }

}
