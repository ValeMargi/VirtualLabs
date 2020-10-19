import { Assignment } from './../../../../models/assignment.model';
import { Component, OnInit, Input,Output} from '@angular/core';
import { Homework } from 'src/app/models/homework.model';
import { HomeworkVersion } from 'src/app/models/homework-version.model';
import { StudentService } from 'src/app/services/student.service';
import { CourseService } from 'src/app/services/course.service';
import { MatDialogRef } from '@angular/material/dialog';
@Component({
  selector: 'app-add-homework-cont',
  templateUrl: './add-homework-cont.component.html',
  styleUrls: ['./add-homework-cont.component.css']
})
export class AddHomeworkContComponent implements OnInit {

  @Input() homeworkToAdd: Homework[] = [];
  assId: number;

  @Output() public HOMEWORK: Homework;
  getHomework( ){
    this.studentService.getHomework(this.courseService.currentCourse.getValue().name,this.assId).subscribe(
      (data) => {
        this.matDialogRef.close();
        console.log("Homework caricato!");
      },
      (error) => {
        console.log("Errore nel caricamento dell'homework");
      }
    );
  }



  constructor(private matDialogRef: MatDialogRef<AddHomeworkContComponent>,
               private studentService: StudentService,
               private courseService: CourseService) { }

  ngOnInit(): void { }

  addVersionH(content: any ) {
    let file: File = content.file;
    let homeworkVersion: HomeworkVersion = content.version;

    this.studentService.uploadVersionHomework(this.courseService.currentCourse.getValue().name,this.assId, this.getHomework().id,file).subscribe(
      (data) => {
        this.matDialogRef.close();
        this.studentService.homeworkCreation.emit(homeworkVersion);
        console.log("Caricamento della Version-homework Completato");
      },
      (error) => {
        console.log("Errore nel caricamento dell'homework");
      }
    );
  }






}
