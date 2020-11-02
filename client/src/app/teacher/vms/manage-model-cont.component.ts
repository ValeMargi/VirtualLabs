import { Component, OnInit, Output } from '@angular/core';
import { CourseService } from 'src/app/services/course.service';
import { Course } from 'src/app/models/course.model';
import { TeacherService } from 'src/app/services/teacher.service';
import { MatDialogRef } from '@angular/material/dialog';
import { core } from '@angular/compiler';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-manage-model-cont',
  templateUrl: './manage-model-cont.component.html',
  styleUrls: ['./manage-model-cont.component.css']
})
export class ManageModelContComponent implements OnInit {

  courseName: string;
  MODEL_VM: Course;

  constructor(private courseService: CourseService, 
              private teacherService: TeacherService,
              private dialogRef: MatDialogRef<ManageModelContComponent>) { }

  ngOnInit(): void {
    this.MODEL_VM = this.courseService.currentCourse.getValue();
    
    this.courseService.getOne(this.courseName).subscribe(
      (data) => {
        this.MODEL_VM = data;
      },
      (error) => {
        window.alert(error.error.message);
      }
    );
  }

  updateModel(modelvm: Course) {
    this.teacherService.updateModelVM(modelvm.name, modelvm).subscribe(
      (data) => {
        this.courseService.currentCourse.next(modelvm);
        this.dialogRef.close();
      },
      (error) => {
        window.alert(error.error.message);
      }
    )
  }

}
