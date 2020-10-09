import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { Teacher } from '../models/teacher.model';
import { AuthService } from '../auth/auth.service';
import { FormGroup } from '@angular/forms';
import { TeacherService } from '../services/teacher.service';

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.css']
})
export class EditProfileComponent implements OnInit, OnChanges {

  @Input() currentUser: any;
  @Input() avatar: any;

  selectedPhoto: File;
  password: any;
  newPassword: any;
  passR: any;

  constructor(private dialogRef: MatDialogRef<EditProfileComponent>, 
              private authService: AuthService) { }

  changePassVisibility: boolean = false;

  ngOnInit(): void {

  }

  ngOnChanges(changes: SimpleChanges) {
    this.avatar = changes.avatar.currentValue;
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
    if (pwd != null && pwd2 != null && pwd.length > 0 && pwd2.length > 0 && pwd != pwd2) {
      return;
    }

    if (pwd.length > 0) {
      let map = new Map<string, string>();
      map.set(actualPwd, pwd);

      this.authService.changeUserPassword(map);
    }

    if (this.selectedPhoto != null) {

      let image = this.selectedPhoto;

      if (!image.type.match("image/jpg") && !image.type.match("image/jpeg") && !image.type.match("image/png")) {
        window.alert("Formato immagine non supportato");
      }
      else {
        this.authService.changeAvatar(image);
      }
    }
  }

}
