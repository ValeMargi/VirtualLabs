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

  checkPasswords(group: FormGroup) {
    let pass = group.controls.password.value;
    let confirmPass = group.controls.confirmPassword.value;

    //le 2 password devono coincidere
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
