import { Component, OnInit, Output, Input } from '@angular/core';
import { Teacher } from 'src/app/models/teacher.model';
import { TeacherService } from 'src/app/services/teacher.service';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { AddCourseDialogComponent } from './add-course-dialog.component';
import { Course } from 'src/app/models/course.model';
import { CourseService } from 'src/app/services/course.service';

@Component({
  selector: 'app-add-course-cont',
  templateUrl: './add-course-cont.component.html',
  styleUrls: ['./add-course-cont.component.css']
})
export class AddCourseContComponent implements OnInit {

  @Output() ALL_TEACHERS: Teacher[] = [];
  @Input() teachersToAdd: Teacher[] = [];

  constructor(public matDialog: MatDialog, 
    private dialogRef: MatDialogRef<AddCourseDialogComponent>, 
    private teacherService: TeacherService,
    private courseService: CourseService) { }

  ngOnInit(): void {
    this.teacherService.all().subscribe(
      (data) => {
        data.splice(data.indexOf(this.teacherService.currentTeacher));
        this.ALL_TEACHERS = data;
      },
      (error) => {
        console.log("Impossibile ottenere gli insegnanti");
      }
    )
  }

  addCourse(course: Course) {
    this.courseService.addCourse(course, this.teachersToAdd.map(teacher => teacher.id)).subscribe(
      (data) => {
        this.courseService.setCurrentCourse(course);
        this.close();
      },
      (error) => {
        console.log(error);
        console.log("corso non aggiunto");
      }
    );
  }

  close() {
    this.dialogRef.close();
  }

}
