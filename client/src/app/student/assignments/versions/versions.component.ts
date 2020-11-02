import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { HomeworkVersion } from 'src/app/models/homework-version.model';
import { HomeworkCorrection } from 'src/app/models/homework-correction.model';
import { Homework } from 'src/app/models/homework.model';
import { Location } from '@angular/common';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { AddHomeworkContComponent } from '../add-homework/add-homework-cont/add-homework-cont.component';
import { ActivatedRoute } from '@angular/router';
import { ViewImageContComponent } from 'src/app/view-image/view-image-cont/view-image-cont.component';

@Component({
  selector: 'app-versions-student',
  templateUrl: './versions.component.html',
  styleUrls: ['./versions.component.css']
})
export class VersionsComponent implements OnInit, OnChanges {

  @Input() homework: Homework;
  @Input() versions: HomeworkVersion[] = [];
  @Input() corrections: HomeworkCorrection[] = [];

  private assId: number;
  corrVisibility: boolean = false;
  corrsToShow: HomeworkCorrection[] = [];
  showedId: number = -1;

  constructor(private location: Location,
              private matDialog: MatDialog,
              private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.assId = +this.route.snapshot.paramMap.get('idA');
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.versions != undefined) {
      this.versions = changes.versions.currentValue;
    }

    if (changes.corrections != undefined) {
      this.corrections = changes.corrections.currentValue;
    }

    if (changes.homework != undefined) {
      this.homework = changes.homework.currentValue;
    }
  }

  uploadVersion() {
    const dialogRef = this.matDialog.open(AddHomeworkContComponent,{ id: 'dialogHomework'});
    const dialogConfig = new MatDialogConfig();

    dialogRef.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogRef.componentInstance.assId = this.assId;
    dialogRef.componentInstance.hwId = this.homework.id;

    dialogConfig.data = {
        id: 1,
        title: 'UploadVersion'
    };
  }

  back() {
    this.location.back();
  }

  showCorrections(version: HomeworkVersion) {
    if (this.showedId == version.id) {
      this.corrVisibility = false;
      this.showedId = -1;
      this.corrsToShow = [];
    }
    else {
      this.corrVisibility = true;
      this.showedId = version.id;
      this.corrsToShow = [];

      this.corrections.forEach(cor => {
        if (cor.versionId == version.id) {
          this.corrsToShow.push(cor);
        }
      });
    }
  }

  openVersionImage(version: HomeworkVersion) {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        title: 'VersionText',
        isTeacher: false,
        type: "version",
        assignmentId: this.assId,
        homeworkId: this.homework.id,
        versionId: version.id
    };

    this.matDialog.open(ViewImageContComponent, dialogConfig);
  } 

  openCorrectionImage(correction: HomeworkCorrection) {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        title: 'CorrectionText',
        isTeacher: false,
        type: "correction",
        assignmentId: this.assId,
        homeworkId: this.homework.id,
        correctionId: correction.id
    };

    this.matDialog.open(ViewImageContComponent, dialogConfig);
  }

}
