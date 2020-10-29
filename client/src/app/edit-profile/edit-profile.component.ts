import { Component, OnInit, Input, OnChanges, SimpleChanges, Output, EventEmitter } from '@angular/core';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { Teacher } from '../models/teacher.model';
import { AuthService } from '../auth/auth.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TeacherService } from '../services/teacher.service';

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.css']
})
export class EditProfileComponent implements OnInit, OnChanges {

  @Input() currentUser: any;
  @Input() avatar: any;
  @Input() querying: boolean;
  @Input() avatar_ok: boolean;
  @Input() pwd_ok: boolean;
  @Output('avatar') changeAvatar = new EventEmitter<any>();
  @Output('password') changePassword = new EventEmitter<any>();

  selectedPhoto: File;


  ChangePasswordForm: FormGroup;

  constructor(private dialogRef: MatDialogRef<EditProfileComponent>,
              private formBuilder: FormBuilder) {

      this.ChangePasswordForm = this.formBuilder.group({
        actualPassword:['', [Validators.required,Validators.minLength(8), Validators.maxLength(20)]],
        newPassword: ['', [Validators.required,Validators.minLength(8), Validators.maxLength(20)]],
        repeatPassword: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(20)]],
      }, { validator: this.checkPasswords });

    }

    checkPasswords(group: FormGroup) { // here we have the 'passwords' group
    let pass = group.controls.newPassword.value;
    let confirmPass = group.controls.repeatPassword.value;

    return pass === confirmPass ? null : { notSame: true }
  }


  changePassVisibility: boolean = false;

  ngOnInit(): void {

  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.avatar != null) {
      this.avatar = changes.avatar.currentValue;
    }

    if (changes.querying != null) {
      this.querying = changes.querying.currentValue;
    }

    if (changes.avatar_ok != null) {
      this.avatar_ok = changes.avatar_ok.currentValue;
    }

    if (changes.pwd_ok != null) {
      this.pwd_ok = changes.pwd_ok.currentValue;
    }
  }

  close() {
    this.dialogRef.close();
  }

  toggleChangePass() {
    this.changePassVisibility = !this.changePassVisibility;
  }

  onFileChanged(imageInput) {
    this.selectedPhoto = imageInput.target.files[0]

    const reader = new FileReader();
    reader.readAsDataURL(this.selectedPhoto);
    reader.onload = (_event) => {
      this.avatar = reader.result;
    }
  }

  save(actualPwd: string, pwd: string, pwd2: string) {
    if (actualPwd.length > 0 && pwd.length > 0 && pwd2.length > 0) {
      if (actualPwd == pwd) {
        window.alert("La nuova password deve essere diversa");
      }
      else if (pwd != pwd2) {
        window.alert("Conferma nuova password non corretta");
      }
      else {
        this.changePassword.emit({oldPassword: actualPwd, newPassword: pwd});
      }
    }

    if (this.selectedPhoto != null) {

      let image = this.selectedPhoto;

      if (!image.type.match("image/jpg") && !image.type.match("image/jpeg") && !image.type.match("image/png")) {
        window.alert("Formato immagine non supportato");
      }
      else {
        this.changeAvatar.emit(image);
      }
    }
  }

}
