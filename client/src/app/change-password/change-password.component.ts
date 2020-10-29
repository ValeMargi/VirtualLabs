import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit, OnChanges {

  @Input() ok: boolean;
  @Input() error: boolean;
  @Input() msg: string;
  @Output('changePass') changePass = new EventEmitter<string>();

  constructor() { }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.ok != null) {
      this.ok = changes.ok.currentValue;
    }

    if (changes.error != null) {
      this.error = changes.error.currentValue;
    }

    if (changes.msg != null) {
      this.msg = changes.msg.currentValue;
    }
  }

  save(newPass: string, passR: string) {
    if (newPass != passR) {
      window.alert("Le 2 password devono coincidere");
      return;
    }
    else if (!this.ChangePasswordForm.valid) {
      window.alert("La password deve essere lunga da 8 a 20 caratteri");
      return;
    }
    else {
      this.changePass.emit(newPass);
    }
  }

}
