import { Component, OnInit, Output } from '@angular/core';
import { TeacherService } from '../services/teacher.service';
import { StudentService } from '../services/student.service';
import { AuthService } from '../auth/auth.service';
import { Md5 } from 'ts-md5/dist/md5';
import { RegisterDialogComponent } from '../register/register-dialog.component';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-edit-profile-cont',
  templateUrl: './edit-profile-cont.component.html',
  styleUrls: ['./edit-profile-cont.component.css']
})
export class EditProfileContComponent implements OnInit {

  private md5: Md5;

  CURRENT_USER: any;
  CURRENT_AVATAR: any;
  QUERYING: boolean = false;
  AVATAR_OK: boolean = false;
  PWD_OK: boolean = false;

  constructor(private authService: AuthService,
              private teacherService: TeacherService,
              private studentService: StudentService,
              private dialogRef: MatDialogRef<RegisterDialogComponent>) { }

  ngOnInit() {
    this.CURRENT_USER = this.authService.getUserByRole();

    const id = localStorage.getItem('currentId');
    const role = localStorage.getItem('role');

    if (role == "student") {
      this.studentService.getOne(id).subscribe(
        (data) => {
          this.CURRENT_AVATAR = 'data:' + data.avatar.type + ';base64,' + data.avatar.picByte;
        },
        (error) => {
          window.alert(error.error.message);
        }
      );
    }
    else {
      this.teacherService.getOne(id).subscribe(
        (data) => {
          this.CURRENT_AVATAR = 'data:' + data.avatar.type + ';base64,' + data.avatar.picByte;
        },
        (error) => {
          window.alert(error.error.message);
        }
      );
    }
  }

  changeAvatar(avatar: File) {
    this.QUERYING = true;
    this.AVATAR_OK = false;

    this.authService.changeAvatar(avatar).subscribe(
      (data) => {
        if (data == false) {
          window.alert("Errore nel cambio dell'avatar, si prega di riprovare");
        }

        this.QUERYING = false;
        this.AVATAR_OK = true;
        setTimeout(()=>{this.close()}, 3000);
      },
      (error) => {
        window.alert(error.error.message);
        this.QUERYING = false;
      }
    )
  }

  changePassword(content: any) {
    this.PWD_OK = false;
    let oldPassword: string = content.oldPassword;
    let newPassword: string = content.newPassword;

    this.md5 = new Md5();

    this.authService.changeUserPassword(this.md5.start().appendStr(oldPassword).end().toString(),
      this.md5.start().appendStr(newPassword).end().toString()).subscribe(
      (data) => {
        if (data == false) {
          window.alert("Errore nel cambio password, si prega di riprovare");
        }

        this.PWD_OK = true;
        setTimeout(()=>{this.close()}, 3000);
      },
      (error) => {
        window.alert(error.error.message);
      }
    )
  }

  close() {
    this.dialogRef.close();
  }

}
