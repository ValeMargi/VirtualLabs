import { Component, OnInit, AfterViewChecked, AfterViewInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { Assignment } from 'src/app/models/assignment.model';
import { TeacherService } from 'src/app/services/teacher.service';
import { CourseService } from 'src/app/services/course.service';
import { FormControl, Validators, FormGroup, FormBuilder } from '@angular/forms';

@Component({
  selector: 'app-create-assignment',
  templateUrl: './create-assignment.component.html',
  styleUrls: ['./create-assignment.component.css']
})
export class CreateAssignmentComponent implements OnInit, AfterViewInit {
  CreateAssignmentForm: FormGroup;

  currentDate;
  oneWeek;

  constructor(
    private matDialogRef: MatDialogRef<CreateAssignmentComponent>, 
    private courseService: CourseService, 
    private teacherService: TeacherService,
    private formBuilder: FormBuilder) { 

      this.CreateAssignmentForm = this.formBuilder.group({
        name : new FormControl('', [Validators.required]),
        currentDate : new FormControl('', [Validators.required]),
        scadenza : new FormControl('', [Validators.required, Validators.min(this.currentDate)]),
       
      });
    }

  ngAfterViewInit() {

  }

  ngOnInit(): void {
    this.currentDate = new Date();
    this.oneWeek = new Date(this.currentDate);
    this.oneWeek.setDate(this.oneWeek.getDate() + 7);
  }

  close() {
    this.matDialogRef.close();
  }

  createAss(name: string, release: string, expire: string) {
    let assignment = new Assignment(-1, name, release, expire);
    this.teacherService.addAssignment(this.courseService.currentCourse.getValue().name, null, assignment); //TODO togliere null
  }
}
