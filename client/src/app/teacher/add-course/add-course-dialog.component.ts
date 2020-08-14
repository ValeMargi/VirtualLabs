import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Course } from 'src/app/models/course.model';
import { CourseService } from 'src/app/services/course.service';
import { Teacher } from 'src/app/models/teacher.model';

@Component({
  selector: 'app-add-course-dialog',
  templateUrl: './add-course-dialog.component.html',
  styleUrls: ['./add-course-dialog.component.css']
})
export class AddCourseDialogComponent implements OnInit {

  allTeachers: Teacher[] = [];
  teacherSelected: Teacher;
  teachersToAdd: string[] = [];

  constructor(public matDialog: MatDialog, 
      private dialogRef: MatDialogRef<AddCourseDialogComponent>,
      private courseService: CourseService) { }

  ngOnInit(): void {
  }

  close() {
    this.dialogRef.close();
  }

  onTeacherSelected(teacher: Teacher) {
    this.teacherSelected = teacher;
  }

  addTeacher() {
    if (this.teacherSelected != null && !this.teachersToAdd.includes(this.teacherSelected.id)) {
      this.teachersToAdd.push(this.teacherSelected.id);
    }
  }

  addCourse(name: string, min: number, max: number) {
    let course = new Course(name, "", min, max, false, 0, 0, 0, 0, 0);
    this.courseService.addCourse(course);
  }

}
