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

  QUERYING: boolean;

  assId: number;
  hwId: number;

  constructor(private matDialogRef: MatDialogRef<AddHomeworkContComponent>,
               private studentService: StudentService,
               private courseService: CourseService) { }

  ngOnInit(): void { 
    this.QUERYING = false;
  }

  addVersionH(file: File) {
    this.QUERYING = true;

    this.studentService.uploadVersionHomework(this.courseService.currentCourse.getValue().name, this.assId, this.hwId, file).subscribe(
      (data) => {
        //se il caricamento va a buon fine, si chiude la dialog e si notifica il componente
        this.QUERYING = false;
        this.matDialogRef.close();
        this.studentService.verUpload.emit(data);
      },
      (error) => {
        window.alert(error.error.message);
        this.QUERYING = false;

        if (error.error.status == 409) {
          //se ricevo 409 Conflict, significa che l'homework è in stato "permanent" e non si possono caricare nuove versioni...
          //...questo serve nel frattempo il professore ha dato una valutazione, o se è scaduto il tempo
          this.matDialogRef.close();
          //si forza un aggiornamento a permanent dell'homework
          this.studentService.verUpload.emit(null);
        }
      }
    );
  }
}
