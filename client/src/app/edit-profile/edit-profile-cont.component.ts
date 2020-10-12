import { Component, OnInit, Output } from '@angular/core';
import { TeacherService } from '../services/teacher.service';
import { StudentService } from '../services/student.service';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-edit-profile-cont',
  templateUrl: './edit-profile-cont.component.html',
  styleUrls: ['./edit-profile-cont.component.css']
})
export class EditProfileContComponent implements OnInit {

  @Output() CURRENT_USER: any;
  @Output() CURRENT_AVATAR: any;
  @Output() QUERYING: boolean = false;
  @Output() AVATAR_OK: boolean = false;
  @Output() PWD_OK: boolean = false;

  constructor(private authService: AuthService, 
              private teacherService: TeacherService,
              private studentService: StudentService) { }

  ngOnInit() {
    this.CURRENT_USER = this.authService.getUserByRole();
  
    let id = localStorage.getItem('currentId');

    if (this.authService.currentUser.role == "student") {
      this.studentService.getOne(id).subscribe(
        (data) => {
          this.CURRENT_AVATAR = 'data:' + data.avatar.type + ';base64,' + data.avatar.picByte;
        },
        (error) => {
          window.alert("Impossibile ottenere l'avatar");
        }
      );
    }
    else {
      this.teacherService.getOne(id).subscribe(
        (data) => {
          this.CURRENT_AVATAR = 'data:' + data.avatar.type + ';base64,' + data.avatar.picByte;
        },
        (error) => {
          window.alert("Impossibile ottenere l'avatar");
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
      },
      (error) => {
        window.alert("Errore nel caricamento dell'immagine");
        this.QUERYING = false;
      }
    )
  }

  changePassword(password: Map<string, string>) {
    this.PWD_OK = false;
    
    this.authService.changeUserPassword(password).subscribe(
      (data) => {
        if (data == false) {
          window.alert("Errore nel cambio password, si prega di riprovare");
        }

        this.PWD_OK = true;
      },
      (error) => {
        window.alert("Errore nel cambio password");
      }
    )
  }

}
