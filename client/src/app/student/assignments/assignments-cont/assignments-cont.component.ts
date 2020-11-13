import { Component, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { AuthService } from '../../../auth/auth.service';
import { Homework } from 'src/app/models/homework.model';
import { Assignment } from 'src/app/models/assignment.model';
import { StudentService } from 'src/app/services/student.service';
import { CourseService } from 'src/app/services/course.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';
import { TeamService } from 'src/app/services/team.service';
import { AssignmentGrade } from 'src/app/models/assignment-grade.model';
@Component({
  selector: 'app-assignments-cont',
  templateUrl: './assignments-cont.component.html',
  styleUrls: ['./assignments-cont.component.css']
})
export class AssignmentsContComponent implements OnInit, OnDestroy {

  HAS_TEAM: boolean = false;
  HAS_VM: boolean = false;
  ASSIGNMENTS: AssignmentGrade[] = [];

  private route$: Subscription;
  private courseName: string;

  constructor(private studentService: StudentService,
              private courseService: CourseService,
              private teamService: TeamService,
              private authService: AuthService,
              private router: Router,
              private route: ActivatedRoute) {

  }

  ngOnInit(): void {
    this.route$ = this.route.params.subscribe(params => {
      this.courseName = params.courses;

      if (this.courseName == undefined) {
        return;
      }

      const currentId: string = localStorage.getItem('currentId');

      this.studentService.allAssignments(this.courseName).subscribe(
        (data) =>  {
          let array: AssignmentGrade[] = new Array();

          data.forEach(ass => {
            let assignment: Assignment = ass.assignment;
            let grade: string = ass.grade;
            let status: string = ass.status;
            let assGrade: AssignmentGrade = new AssignmentGrade(assignment.id, assignment.assignmentName, assignment.releaseDate, assignment.expiration, grade, status);
            array.push(assGrade);
          });

          this.ASSIGNMENTS = array;

          this.teamService.getTeamForStudent(this.courseName, currentId).subscribe(
            (data) => {
              if (data != null) {
                this.HAS_TEAM = true;
    
                this.teamService.getAllVMTeam(this.courseName, data.id).subscribe(
                  (data) => {
                    if (data.length > 0) {
                      this.HAS_VM = true;
                    }
                    else {
                      this.HAS_VM = false;
                    }
                  },
                  (error) => {
                    window.alert(error.error.message);
                  }
                )
              }
              else {
                this.HAS_TEAM = false;
                this.HAS_VM = false;
              }
            },
            (error) => {
              window.alert(error.error.message);
              const status: number = error.error.status;
    
              if (status == 404 || status == 403) {
                this.router.navigateByUrl("home");
                this.courseService.courseReload.emit();
              }
            }
          );
        },
        (error) => {
          window.alert(error.error.message);
          const status: number = error.error.status;

          if (status == 404 || status == 403) {
            this.router.navigateByUrl("home");
            this.courseService.courseReload.emit();
          }
        }
      );
    });
  }

  updateAssignments() {
    if (this.authService.isLoggedOut()) {
      return;
    }

    this.studentService.allAssignments(this.courseName).subscribe(
      (data) =>  {
        let array: AssignmentGrade[] = new Array();

        data.forEach(ass => {
          let assignment: Assignment = ass.assignment;
          let grade: string = ass.grade;
          let status: string = ass.status;
          let assGrade: AssignmentGrade = new AssignmentGrade(assignment.id, assignment.assignmentName, assignment.releaseDate, assignment.expiration, grade, status);
          array.push(assGrade);
        });

        this.ASSIGNMENTS = array;
      },
      (error) => {
        window.alert(error.error.message);
          const status: number = error.error.status;

          if (status == 404 || status == 403) {
            this.router.navigateByUrl("home");
            this.courseService.courseReload.emit();
          }
      });
  }

  ngOnDestroy() {
    this.route$.unsubscribe();
  }
}
