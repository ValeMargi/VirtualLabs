import { Homework } from './../../../models/homework.model';
import { Component,OnInit,AfterViewInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { StudentService } from 'src/app/services/student.service';
import { CourseService } from 'src/app/services/course.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-add-homework',
  templateUrl: './add-homework.component.html',
  styleUrls: ['./add-homework.component.css']
})
export class AddHomeworkComponent implements OnInit, AfterViewInit {

  //public HOMEWORKS: Homeworks[] = [];

  form = {
  name : new FormControl('', [Validators.required, Validators.minLength(3)]),
  file : new FormControl('', [Validators.required])
  }
  getErrorMessage() {
    if (this.form.name.hasError('required') || this.form.file.hasError('required')) {
      return 'Campo obbligatorio';
    }
    if(this.form.name.hasError('minlength')){
      return 'Inserire almeno 3 caratteri';
    }
  }

  currentDate; oneWeek;
  constructor(private matDialogRef: MatDialogRef<AddHomeworkComponent>,
              private studentService: StudentService,
              private courseService: CourseService,
              private route: ActivatedRoute) { }

  ngAfterViewInit() {}

  ngOnInit(): void {
    this.currentDate = new Date();
    this.oneWeek = new Date(this.currentDate);
    this.oneWeek.setDate(this.oneWeek.getDate() + 7);
  }

  close() {
    console.log("Inserimento Annullato");
    this.matDialogRef.close();
  }
/*
  addHomework() {

    this.studentService.addHomework.subscribe(
      (data) => {
        if (this.HOMEWORKS.length == 0) {
          let array: Homeworks[] = new Array();
          array.push(data);
          this.HOMEWORKS = array;
        }
        else {
          this.HOMEWORKS.push(data);
        }
      }
    );
    this.matDialogRef.close();
  }*/

  onFileSelected(event){
    console.log(event);
  }
}
