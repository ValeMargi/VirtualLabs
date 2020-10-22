import { Component, Inject, Input, OnInit, Output } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { HomeworkVersion } from 'src/app/models/homework-version.model';
import { CourseService } from 'src/app/services/course.service';
import { TeacherService } from 'src/app/services/teacher.service';

@Component({
  selector: 'app-upload-correction-cont',
  templateUrl: './upload-correction-cont.component.html',
  styleUrls: ['./upload-correction-cont.component.css']
})
export class UploadCorrectionContComponent implements OnInit {

  versions: HomeworkVersion[];
  assId: number;
  hwId: number;

  @Output() QUERYING: boolean;
 
  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
              private teacherService: TeacherService,
              private courseService: CourseService,
              private dialogRef: MatDialogRef<UploadCorrectionContComponent>) { }

  ngOnInit(): void {
    this.QUERYING = false;
    this.versions = this.data.versions;
    this.assId = this.data.assId;
    this.hwId = this.data.hwId;
  }

  uploadCorrection(content: any) {
    this.QUERYING = true;
    let file: File = content.file;
    let grade: string = content.grade;
    let version = new HomeworkVersion(-1, "1980-01-01 00:00:00.000", "");

    this.versions.forEach(ver => {
      if (ver.timestamp > version.timestamp) {
        version = ver;
      }
    });

    let permanent: boolean = false;
    if (grade == null || grade.length == 0) {
      grade = "-1";
    }
    else if (grade.length > 0) {
      permanent = true;
    }

    this.teacherService.uploadCorrection(this.courseService.currentCourse.getValue().name, this.assId, this.hwId, version.id, file, permanent, grade).subscribe(
      (data) => {
        this.QUERYING = false;
        this.dialogRef.close();
        this.teacherService.corrUpload.emit({corr: data, permanent: permanent});
      },
      (error) => {
        this.QUERYING = false;
        window.alert("Errore nel caricamento della correzione");
      }
    )
  }

}
