import { Component, OnInit, Output } from '@angular/core';
import { Teacher } from 'src/app/models/teacher.model';
import { TeacherService } from 'src/app/services/teacher.service';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { AddCourseDialogComponent } from './add-course-dialog.component';

@Component({
  selector: 'app-add-course-cont',
  templateUrl: './add-course-cont.component.html',
  styleUrls: ['./add-course-cont.component.css']
})
export class AddCourseContComponent implements OnInit {

  @Output() ALL_TEACHERS: Teacher[] = [];

  constructor(public matDialog: MatDialog, 
    private dialogRef: MatDialogRef<AddCourseDialogComponent>, teacherService: TeacherService) { }

  ngOnInit(): void {
    this.ALL_TEACHERS.push(new Teacher("t01", "Baldi", "Mario", "dsf"));
  }

  close() {
    this.dialogRef.close();
  }

}
