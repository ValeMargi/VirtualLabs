import { Component, OnDestroy, OnInit } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { Homework } from 'src/app/models/homework.model';
import { Assignment } from 'src/app/models/assignment.model';
import { TeacherService } from 'src/app/services/teacher.service';
import { CourseService } from 'src/app/services/course.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-assignments-cont',
  templateUrl: './assignments-cont.component.html',
  styleUrls: ['./assignments-cont.component.css']
})
export class AssignmentsContComponent implements OnInit, OnDestroy {

  public ASSIGNMENTS: Assignment[] = []

  private route$: Subscription

  constructor(private teacherService: TeacherService,
              private router: Router,
              private route: ActivatedRoute) { 
    
  }

  ngOnInit(): void {
    this.route$ = this.route.params.subscribe(params => {
      let courseName = params.courses;

      if (courseName == undefined) {
        return;
      }
    
      this.teacherService.allAssignments(courseName).subscribe(
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

      this.teacherService.assCreation.subscribe(
        (data) => {
          let array: Assignment[] = this.ASSIGNMENTS;
          this.ASSIGNMENTS = new Array();
          array.push(data);

          array.forEach(ass => {
            this.ASSIGNMENTS.push(ass);
          });
        }, 
        (error) => {

        }
      );
    });
  }

  ngOnDestroy() {
    this.route$.unsubscribe();
  }

}
