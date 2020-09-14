import { Component, OnInit, Input, Output} from '@angular/core';
import { Homework } from 'src/app/models/homework.model';
import { HomeworkVersion } from 'src/app/models/homework-version.model';
import { StudentService } from 'src/app/services/student.service';
@Component({
  selector: 'app-add-homework-cont',
  templateUrl: './add-homework-cont.component.html',
  styleUrls: ['./add-homework-cont.component.css']
})
export class AddHomeworkContComponent implements OnInit {

  @Input() homeworkToAdd: Homework[] = [];

  constructor(private studentService: StudentService,) { }

  ngOnInit(): void {
  
  }

  addAssignment(content: any) {
    let homeworkVersion: HomeworkVersion = content.version;
    let file: File = content.file;


  }


}
