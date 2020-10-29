import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

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

  ChangePasswordForm: FormGroup;

  constructor( private formBuilder: FormBuilder) {

    this.ChangePasswordForm = this.formBuilder.group({
      password: ['', [Validators.required,Validators.minLength(8), Validators.maxLength(20)]],
      confirmPassword: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(20)]],
    }, { validator: this.checkPasswords });
  }

  checkPasswords(group: FormGroup) { // here we have the 'passwords' group
    let pass = group.controls.password.value;
    let confirmPass = group.controls.confirmPassword.value;

    return pass === confirmPass ? null : { notSame: true }
  }

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
    if (newPass.length == 0 || passR.length == 0) {
      window.alert("Riempire entrambi i campi");
    }
    else if (newPass != passR) {
      window.alert("Le 2 password devono coincidere");
    }
    else {
      this.changePass.emit(newPass);
    }
  }

}
