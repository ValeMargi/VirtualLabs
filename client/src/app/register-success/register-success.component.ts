import { Component, OnDestroy, OnInit } from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';
import { AuthService } from '../auth/auth.service';


@Component({
  selector: 'app-register-success',
  templateUrl: './register-success.component.html',
  styleUrls: ['./register-success.component.css']
})
export class RegisterSuccessComponent implements OnInit, OnDestroy {

  private route$: Subscription;
  success: boolean = true;

  constructor(private route: ActivatedRoute) { }

  ngOnInit(): void {    
    this.route$ = this.route.queryParams.subscribe(param => {
      if (param['confirmToken']) {
        //se nei query params c'è il token di conferma si mostra una scritta di successo...
        this.success = true;
      }
      else if (param['expToken']) {
        //...altrimenti di errore
        this.success = false;
      }
    })
  }

  ngOnDestroy() {
    this.route$.unsubscribe();
  }

}
