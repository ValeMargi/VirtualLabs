import { Component, OnInit } from '@angular/core';
import { Assignment } from 'src/app/models/assignment.model';
import { TeacherService } from 'src/app/services/teacher.service';
import { CourseService } from 'src/app/services/course.service';
import { MatDialogRef } from '@angular/material/dialog';
import { CreateAssignmentComponent } from './create-assignment.component';

@Component({
  selector: 'app-create-assignment-cont',
  templateUrl: './create-assignment-cont.component.html',
  styleUrls: ['./create-assignment-cont.component.css']
})
export class CreateAssignmentContComponent implements OnInit {

  constructor(private matDialogRef: MatDialogRef<CreateAssignmentComponent>, private teacherService: TeacherService, private courseService: CourseService) { }

  ngOnInit(): void {
  }

  createAss(content: any) {
    let file: File = content.file;
    let assignment: Assignment = content.assignment;

    this.teacherService.addAssignment(this.courseService.currentCourse.getValue().name, file, assignment).subscribe(
      (data) => {
        this.matDialogRef.close();
        this.teacherService.assCreation.emit(assignment);
      }, 
      (error) => {
        console.log("Errore nella creazione della consegna");
      }
    )
  }

}
