import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../auth/auth.service';
import { Homework } from 'src/app/models/homework.model';
import { Assignment } from 'src/app/models/assignment.model';
import { StudentService } from 'src/app/services/student.service';
import { CourseService } from 'src/app/services/course.service';
import { ActivatedRoute } from '@angular/router';
@Component({
  selector: 'app-assignments-cont',
  templateUrl: './assignments-cont.component.html',
  styleUrls: ['./assignments-cont.component.css']
})
export class AssignmentsContComponent implements OnInit {

  public HOMEWORKS: Homework[] = []
  public ASSIGNMENTS: Assignment[] = []

  constructor(private studentService: StudentService,
              private courseService: CourseService,
              private route: ActivatedRoute) {

  }

  ngOnInit(): void {
    this.route.params.subscribe( params => console.log(params));

    this.studentService.allAssignments(this.courseService.currentCourse.getValue().name).subscribe(
      (data) =>  {
        this.ASSIGNMENTS = data;
      },
      (error) => {
        console.log("Impossibile ottenere gli assignments");
      }
    );
  }

}
