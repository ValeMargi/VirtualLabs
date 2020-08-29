import { Component, OnInit, Input } from '@angular/core';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { Teacher } from '../models/teacher.model';
import { AuthService } from '../auth/auth.service';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.css']
})
export class EditProfileComponent implements OnInit {

  @Input() currentUser: any;
  @Input() avatar: any;

  selectedPhoto: File;
  previewPhoto: any;

  password: any;
  newPassword: any;
  passR: any;

  constructor(private matDialog: MatDialog, private dialogRef: MatDialogRef<EditProfileComponent>, private authService: AuthService) { }

  changePassVisibility: boolean = false;

  ngOnInit(): void {
    const reader = new FileReader();
    reader.readAsDataURL(new Blob([this.avatar]));
    reader.onload = (_event) => { 
      this.previewPhoto = reader.result; 
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
      this.previewPhoto = reader.result; 
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
        console.log("tipo errato");
        //mostrare errore
      }
      else {
        this.authService.changeAvatar(image);
      }
    }
  }

}
