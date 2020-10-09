import { Component, OnInit, Output } from '@angular/core';
import { Teacher } from '../models/teacher.model';
import { TeacherService } from '../services/teacher.service';
import { StudentService } from '../services/student.service';
import { AuthService } from '../auth/auth.service';
import { User } from '../models/user.model';

@Component({
  selector: 'app-edit-profile-cont',
  templateUrl: './edit-profile-cont.component.html',
  styleUrls: ['./edit-profile-cont.component.css']
})
export class EditProfileContComponent implements OnInit {

  @Output() CURRENT_USER: any;
  @Output() CURRENT_AVATAR: any;

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

}
