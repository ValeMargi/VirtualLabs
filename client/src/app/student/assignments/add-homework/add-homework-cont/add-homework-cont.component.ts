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

  @Input() homework: Homework;
  @Output() QUERYING: boolean;
  assId: number;

  constructor(private matDialogRef: MatDialogRef<AddHomeworkContComponent>,
               private studentService: StudentService,
               private courseService: CourseService) { }

  ngOnInit(): void { 
    this.QUERYING = false;
  }

  addVersionH(file: File) {
    this.QUERYING = true;

    this.studentService.getHomework(this.courseService.currentCourse.getValue().name, this.assId).subscribe(
      (data) => {
        this.homework = data;
        this.studentService.uploadVersionHomework(this.courseService.currentCourse.getValue().name, this.assId, this.homework.id, file).subscribe(
          (data) => {
            console.log(data)
            this.QUERYING = false;
            this.matDialogRef.close();
            this.studentService.verUpload.emit(data);
          },
          (error) => {
            this.QUERYING = false;
            window.alert("Errore nel caricamento dell'homework");
          }
        );
      },
      (error) =>{
        this.QUERYING = false;
        window.alert("Errore nel reperire l'homework")
      }
    );
  }
}
