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

  constructor(private authService: AuthService,
              private route: ActivatedRoute) { }

  ngOnInit(): void {    
    this.route$ = this.route.params.subscribe(param => {
      if (param.success == "confirm") {
        this.success = true;
      }
      else {
        this.success = false;
      }
    })
  }

  ngOnDestroy() {
    this.route$.unsubscribe();
  }

}
