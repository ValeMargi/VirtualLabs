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

  constructor(private dialogRef: MatDialogRef<AddCourseDialogComponent>, 
              private teacherService: TeacherService,
              private courseService: CourseService) { }

  ngOnInit(): void {
    this.teacherService.all().subscribe(
      (data) => {
        this.ALL_TEACHERS = data;
      },
      (error) => {
        console.log("Impossibile ottenere gli insegnanti");
      }
    )
  }

  addCourse(content: any) {
    let course: Course = content.course;
    let file: File = content.file;
    let ids: string[] = content.ids;

    if (course.min < 0 || course.max <= 0 || course.name.length == 0 || course.acronym.length == 0) {
      window.alert("Controllare che i dati inseriti siano validi e riprovare");
      return;
    }

    this.courseService.addCourse(course, ids).subscribe(
      (data) => {
        this.courseService.setCurrentCourse(course);
        
        this.teacherService.addModelVM(course.name, file, course).subscribe(
          (data) => {
            this.close();
          },
          (error) => {
            window.alert("Errore aggiunta modello VM");
          }
        );
      },
      (error) => {
        window.alert("Errore aggiunta corso");
      }
    );
  }

  close() {
    this.dialogRef.close();
  }

}
