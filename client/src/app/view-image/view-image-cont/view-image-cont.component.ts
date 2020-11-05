import { Component, Inject, Input, OnInit, Output } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
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
  VMSTUDENT: boolean = false;
  QUERYING: boolean = false;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
              private teacherService: TeacherService,
              private studentService: StudentService,
              private courseService: CourseService,
              private sanitizer: DomSanitizer) { }

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
              this.PHOTO = this.sanitizer.bypassSecurityTrustUrl('data:' + data.type + ';base64,' + data.picByte);
            },
            (error) => {
              window.alert(error.error.message);
            }
          );
        }
        else {
          this.studentService.getAssignment(course, assignmentId).subscribe(
            (data) => {
              this.TIMESTAMP = data.timestamp;
              this.PHOTO = this.sanitizer.bypassSecurityTrustUrl('data:' + data.type + ';base64,' + data.picByte);
            },
            (error) => {
              window.alert(error.error.message);
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
            this.PHOTO = this.sanitizer.bypassSecurityTrustUrl('data:' + data.type + ';base64,' + data.picByte);
          },
          (error) => {
            window.alert(error.error.message);
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
            this.PHOTO = this.sanitizer.bypassSecurityTrustUrl('data:' + data.type + ';base64,' + data.picByte);
          },
          (error) => {
            window.alert(error.error.message);
          }
        );

        this.TITLE = "Correzione dell'homework";

        break;
      }
      case "vm": {
        let isTeacher: boolean = this.data.isTeacher;
        let vmId: number = this.data.vmId;

        if (isTeacher) {
          this.VMSTUDENT = false;

          this.teacherService.getVMForProfessor(course, vmId).subscribe(
            (data) => {
              this.TIMESTAMP = data.timestamp;
              this.PHOTO = this.sanitizer.bypassSecurityTrustUrl('data:' + data.type + ';base64,' + data.picByte);
            },
            (error) => {
              window.alert(error.error.message);
            }
          );
        }
        else {
          this.VMSTUDENT = true;

          this.studentService.getVMForStudent(course, vmId).subscribe(
            (data) => {
              this.TIMESTAMP = data.timestamp;
              this.PHOTO = this.sanitizer.bypassSecurityTrustUrl('data:' + data.type + ';base64,' + data.picByte);
            },
            (error) => {
              window.alert(error.error.message);
            }
          );
        }

        this.TITLE = "Schermata VM";

        break;
      }
    }
  }

  useVM(file: File) {
    this.QUERYING = true;

    this.studentService.useVM(this.courseService.currentCourse.getValue().name, this.data.vmId, file).subscribe(
      (data) => {
        if (data) {
          const reader = new FileReader();
          reader.readAsDataURL(file);
          reader.onload = (_event) => { 
            this.PHOTO = reader.result; 
          }
        }
        else {
          window.alert("Impossibile utilizzare la VM, riprovare");
        }

        this.QUERYING = false;
      },
      (error) => {
        window.alert(error.error.message);
        this.QUERYING = false;
      }
    );
  }

}
