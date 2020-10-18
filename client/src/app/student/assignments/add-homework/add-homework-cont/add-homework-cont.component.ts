import { Component, OnInit, Input} from '@angular/core';
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

  constructor(private matDialogRef: MatDialogRef<AddHomeworkContComponent>,
               private studentService: StudentService,
               private courseService: CourseService) { }

  ngOnInit(): void { }

  addVersionH(content: any) {
    let file: File = content.file;
    let homeworkVersion: HomeworkVersion = content.version;

    this.studentService.addHomework(this.courseService.currentCourse.getValue().name, file, homeworkVersion).subscribe(
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
