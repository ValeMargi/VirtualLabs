import { Component, Inject, Input, OnInit, Output } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CourseService } from 'src/app/services/course.service';
import { StudentService } from 'src/app/services/student.service';
import { TeacherService } from 'src/app/services/teacher.service';

@Component({
  selector: 'app-view-image-cont',
  templateUrl: './view-image-cont.component.html',
  styleUrls: ['./view-image-cont.component.css']
})
export class ViewImageContComponent implements OnInit {
  PHOTO: any;
  TITLE: string;
  TIMESTAMP: string;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
              private teacherService: TeacherService,
              private studentService: StudentService,
              private courseService: CourseService) { }

  ngOnInit(): void {
    let type: string = this.data.type; //type can be: assignment, version, correction, vm  
    let course: string = this.courseService.currentCourse.getValue().name;

    switch(type) {
      case "assignment": {
        let assignmentId: number = this.data.assignmentId;
        let isTeacher: boolean = this.data.isTeacher;

        if (isTeacher) {
          this.teacherService.getPhotoAssignment(course, assignmentId).subscribe(
            (data) => {
              this.TIMESTAMP = data.timestamp;
              this.PHOTO = 'data:' + data.type + ';base64,' + data.picByte;
            },
            (error) => {
              window.alert("Impossibile ottenere il testo dell'assignment");
            }
          );
        }
        else {
          this.studentService.getAssignment(course, assignmentId).subscribe(
            (data) => {
              this.TIMESTAMP = data.timestamp;
              this.PHOTO = 'data:' + data.type + ';base64,' + data.picByte;
            },
            (error) => {
              window.alert("Impossibile ottenere il testo dell'assignment");
            }
          );
        }

        this.TITLE = "Testo della consegna";

        break;
      }
      case "version": {
        let assignmentId: number = this.data.assignmentId;
        let homeworkId: number = this.data.homeworkId;
        let versionId: number = this.data.versionId;
        
        this.courseService.getVersionHM(course, assignmentId, homeworkId, versionId).subscribe(
          (data) => {
            this.TIMESTAMP = data.timestamp;
            this.PHOTO = 'data:' + data.type + ';base64,' + data.picByte;
          },
          (error) => {
            window.alert("Impossibile ottenere la versione dell'homework");
          }
        );

        this.TITLE = "Versione dell'homework";

        break;
      }
      case "correction": {
        let assignmentId: number = this.data.assignmentId;
        let homeworkId: number = this.data.homeworkId;
        let correctionId: number = this.data.correctionId;

        this.courseService.getCorrectionHM(course, assignmentId, homeworkId, correctionId).subscribe(
          (data) => {
            this.TIMESTAMP = data.timestamp;
            this.PHOTO = 'data:' + data.type + ';base64,' + data.picByte;
          },
          (error) => {
            window.alert("Impossibile ottenere la correzione dell'homework");
          }
        );

        this.TITLE = "Correzione dell'homework";

        break;
      }
      case "vm": {
        let isTeacher: boolean = this.data.isTeacher;
        let vmId: number = this.data.vmId;

        if (isTeacher) {
          
        }
        else {
          this.studentService.getVMForStudent(course, vmId).subscribe(
            (data) => {
              this.PHOTO = 'data:' + data.type + ';base64,' + data.picByte;
            },
            (error) => {
              window.alert("Impossibile ottenere la schermata della VM");
            }
          );
        }

        this.TITLE = "Schermata VM";

        break;
      }
    }
  }

}