import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { Homework } from 'src/app/models/homework.model';
import { Assignment } from 'src/app/models/assignment.model';
import { TeacherService } from 'src/app/services/teacher.service';
import { CourseService } from 'src/app/services/course.service';

@Component({
  selector: 'app-assignments-cont',
  templateUrl: './assignments-cont.component.html',
  styleUrls: ['./assignments-cont.component.css']
})
export class AssignmentsContComponent implements OnInit {

  public ASSIGNMENTS: Assignment[] = []

  constructor(public teacherService: TeacherService,
              public courseService: CourseService) { 
    
  }

  ngOnInit(): void {
    //provvisorio
    //this.ASSIGNMENTS.push(new Assignment(-1, "Laboratorio 1", "01/01/2020", "31/07/2020"));
    //this.ASSIGNMENTS.push(new Assignment(-1, "Laboratorio 2", "01/01/2020", "31/07/2020"));

    this.teacherService.allAssignments(this.courseService.currentCourse.getValue().name).subscribe(
      (data) =>  {
        this.ASSIGNMENTS = data;
      },
      (error) => {
        console.log("Impossibile ottenere gli assignments");
      }
    )
  }

}
