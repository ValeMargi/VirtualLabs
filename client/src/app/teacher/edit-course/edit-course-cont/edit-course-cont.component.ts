import { Component, OnInit, Output } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';
import { Course } from 'src/app/models/course.model';
import { Teacher } from 'src/app/models/teacher.model';
import { CourseService } from 'src/app/services/course.service';
import { TeacherService } from 'src/app/services/teacher.service';

@Component({
  selector: 'app-edit-course-cont',
  templateUrl: './edit-course-cont.component.html',
  styleUrls: ['./edit-course-cont.component.css']
})
export class EditCourseContComponent implements OnInit {
  COURSE: Course;
  ALL_TEACHERS: Teacher[] = [];
  COURSE_TEACHERS: Teacher[] = [];

  constructor(private courseService: CourseService,
              private teacherService: TeacherService,
              private matDialogRef: MatDialogRef<EditCourseContComponent>,
              private router: Router,
              private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.COURSE = this.courseService.currentCourse.getValue();

    if (this.COURSE == null || this.COURSE.min == -1 || this.COURSE.max == -1) {
      this.courseService.getOne(this.COURSE.name).subscribe(
        (data) => {
          this.COURSE = data;
          this.courseService.setCurrentCourse(data);
          this.getTeachers();
        },
        (error) => {
          window.alert(error.error.message);
        }
      )
    }
    else {
      this.getTeachers();
    }
  }

  getTeachers() {
    this.teacherService.all().subscribe(
      (data) => {
        this.ALL_TEACHERS = data;
      },
      (error) => {
        window.alert(error.error.message);
      }
    )

    this.courseService.getProfessorsForCourse(this.COURSE.name).subscribe(
      (data) => {
        this.COURSE_TEACHERS = data;
      },
      (error) => {
        window.alert(error.error.message);
      }
    )
  }

  enableCourse() {
    let enabled = (this.COURSE.enabled) == 0;

    this.courseService.enableCourse(this.COURSE.name, enabled).subscribe(
      (data) => {
        this.COURSE.enabled = (enabled) ? 1 : 0;
        this.courseService.setCurrentCourse(this.COURSE);
      },
      (error) => {
        window.alert(error.error.message);
      }
    )
  }

  deleteCourse() {
    this.courseService.removeCourse(this.courseService.currentCourse.getValue().name).subscribe(
      (data) => {
        if (data) {
          this.courseService.courseRemove.emit(this.courseService.currentCourse.getValue().name);
          this.matDialogRef.close();
        }
        else {
          window.alert("Errore nell'eliminazione del corso");
        }
      },
      (error) => {
        window.alert(error.error.message);
      }
    );
  }

  addTeachers(teachers: string[]) {
    this.courseService.addProfessorsToCourse(this.COURSE.name, teachers).subscribe(
      (data) => {
        if (data.length != teachers.length) {
          window.alert("Alcuni professori non sono stati aggiunti");
        }
      },
      (error) => {
        window.alert(error.error.message);
      }
    );
  }

  editCourse(content: any) {
    let acronym: string = content.acronym;
    let min: number = content.min;
    let max: number = content.max;

    let c: Course = this.COURSE;
    c.acronym = acronym;
    c.max = max;
    c.min = min;


    this.courseService.modifyCourse(this.COURSE.name, c).subscribe(
      (data) => {
        if (data) {
          this.COURSE = c;
          this.courseService.setCurrentCourse(c);
          this.matDialogRef.close();
        }
        else {
          window.alert("I parametri non sono stati modificati");
        }
      },
      (error) => {
        window.alert(error.error.message);
      }
    )

  }

}
