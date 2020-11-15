import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';
import { AuthService } from 'src/app/auth/auth.service';
import { Md5 } from 'ts-md5/dist/md5';

@Component({
  selector: 'app-change-password-cont',
  templateUrl: './change-password-cont.component.html',
  styleUrls: ['./change-password-cont.component.css']
})
export class ChangePasswordContComponent implements OnInit, OnDestroy {

  private route$: Subscription;
  private token: string;
  private md5: Md5;

  OK: boolean = false;
  ERROR: boolean = false;
  MSG: string;

  constructor(private authService: AuthService,
              private route: ActivatedRoute) { }


  ngOnInit(): void {
    this.route$ = this.route.queryParams.subscribe(params => {
      let token = params['token'];
      let msg: string = params['error'];

      if (token) {
        //se nella url è presente il token lo si recupera...
        this.token = token;
      }
      else if (msg) {
        //...altrimenti si deve mostrare il messaggio di errore
        this.MSG = msg.split("-").join(" ");
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

    this.md5 = new Md5();

    this.authService.savePassword(this.token, this.md5.start().appendStr(password).end().toString()).subscribe(
      (data) => {
        if (data) {
          this.OK = true;
          this.ERROR = false;
          this.authService.storeUrl("home");
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
