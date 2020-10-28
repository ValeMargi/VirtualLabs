import { Component, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { AuthService } from '../../../auth/auth.service';
import { Homework } from 'src/app/models/homework.model';
import { Assignment } from 'src/app/models/assignment.model';
import { StudentService } from 'src/app/services/student.service';
import { CourseService } from 'src/app/services/course.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';
@Component({
  selector: 'app-assignments-cont',
  templateUrl: './assignments-cont.component.html',
  styleUrls: ['./assignments-cont.component.css']
})
export class AssignmentsContComponent implements OnInit, OnDestroy {

  @Output() HOMEWORK: Homework;
  public ASSIGNMENTS: Assignment[] = [];

  private route$: Subscription;

  constructor(private studentService: StudentService,
              private courseService: CourseService,
              private router: Router,
              private route: ActivatedRoute) {

  }

  ngOnInit(): void {
    this.route$ = this.route.params.subscribe(params => {
      let courseName: string = params.courses;

      if (courseName == undefined) {
        return;
      }

      this.studentService.allAssignments(courseName).subscribe(
        (data) =>  {
          this.ASSIGNMENTS = data;
        },
        (error) => {
          window.alert(error.error.message);
          const status: number = error.error.status;

          if (status == 404 || status == 403) {
            this.router.navigateByUrl("home");
          }
        }
      );
    });
  }

  ngOnDestroy() {
    this.route$.unsubscribe();
  }

  getHomework(ass: Assignment) {
    this.studentService.getHomework(this.courseService.currentCourse.getValue().name, ass.id).subscribe(
      (data) =>  {
        this.HOMEWORK = data;
      },
      (error) => {
        window.alert("Errore ottenimento Homework");
      }
    );

  }
}
