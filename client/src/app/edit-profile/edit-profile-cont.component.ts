import { Component, OnInit, Output } from '@angular/core';
import { Teacher } from '../models/teacher.model';
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

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
    this.CURRENT_USER = this.authService.getUserByRole();
  }

}
