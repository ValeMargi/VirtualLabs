import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { Homework } from 'src/app/models/homework.model';
import { Assignment } from 'src/app/models/assignment.model';
import { TeacherService } from 'src/app/services/teacher.service';
import { CourseService } from 'src/app/services/course.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-assignments-cont',
  templateUrl: './assignments-cont.component.html',
  styleUrls: ['./assignments-cont.component.css']
})
export class AssignmentsContComponent implements OnInit {

  public ASSIGNMENTS: Assignment[] = []

  constructor(private teacherService: TeacherService,
              private courseService: CourseService,
              private route: ActivatedRoute) { 
    
  }

  ngOnInit(): void {
    this.route.params.subscribe( params => console.log(params));
    
    this.teacherService.allAssignments(this.courseService.currentCourse.getValue().name).subscribe(
      (data) =>  {
        this.ASSIGNMENTS = data;
      },
      (error) => {
        console.log("Impossibile ottenere gli assignments");
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
  }

}
