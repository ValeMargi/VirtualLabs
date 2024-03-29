import { Component, OnInit, Output, Input, OnDestroy } from '@angular/core';
import { Homework } from 'src/app/models/homework.model';
import { Assignment } from 'src/app/models/assignment.model';
import { TeacherService } from 'src/app/services/teacher.service';
import { CourseService } from 'src/app/services/course.service';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';
import { Student } from 'src/app/models/student.model';
import { HomeworkStudent } from 'src/app/models/homework-student.model';
import { AuthService } from 'src/app/auth/auth.service';

@Component({
  selector: 'app-homeworks-cont',
  templateUrl: './homeworks-cont.component.html',
  styleUrls: ['./homeworks-cont.component.css']
})
export class HomeworksContComponent implements OnInit, OnDestroy {

  private route$: Subscription;

  ASSIGNMENT: Assignment;
  HOMEWORKS_STUDENTS: HomeworkStudent[] = [];

  constructor(private teacherService: TeacherService,
              private courseService: CourseService,
              private authService: AuthService,
              private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route$ = this.route.params.subscribe(params => {
      let idA = params.idA;

      if (idA == null) {
        return;
      }

      this.teacherService.getAssignment(this.courseService.currentCourse.getValue().name, idA).subscribe(
        (data) => {
          this.ASSIGNMENT = data;
          this.updateHomeworks(idA);
        },
        (error) => {
          window.alert(error.error.message);
        }
      );
    });
  }

  ngOnDestroy() {
    this.route$.unsubscribe();
  }

  updateHomeworks(idA: number) {
    if (this.authService.isLoggedOut()) {
      return;
    }

    this.teacherService.allHomework(this.courseService.currentCourse.getValue().name, idA).subscribe(
      (data) => {
        let tmp: HomeworkStudent[] = [];
        data.forEach(element => {
          let homework: Homework = element.Homework;
          let student: Student = element.Student;
          let hws: HomeworkStudent = new HomeworkStudent(student.id, student.firstName, student.name, student.email, homework.id, homework.status, homework.permanent, homework.grade, homework.timestamp);
          tmp.push(hws);
        });

        tmp.sort((a, b) => b.timestamp.localeCompare(a.timestamp));
        this.HOMEWORKS_STUDENTS = tmp;
      },
      (error) => {
        window.alert(error.error.message);
      }
    );
  }

}
