import { AuthService } from 'src/app/auth/auth.service';
import { Component, OnInit, Input, OnChanges, SimpleChanges, Output, EventEmitter, OnDestroy } from '@angular/core';
import { HomeworkVersion } from 'src/app/models/homework-version.model';
import { HomeworkCorrection } from 'src/app/models/homework-correction.model';
import { Location } from '@angular/common';
import { Homework } from 'src/app/models/homework.model';
import { UploadCorrectionContComponent } from './upload-correction/upload-correction-cont/upload-correction-cont.component';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';
import { ViewImageContComponent } from 'src/app/view-image/view-image-cont/view-image-cont.component';

@Component({
  selector: 'app-versions-teacher',
  templateUrl: './versions.component.html',
  styleUrls: ['./versions.component.css']
})
export class VersionsComponent implements OnInit, OnChanges, OnDestroy {

  @Input() homework: Homework;
  @Input() versions: HomeworkVersion[] = [];
  @Input() corrections: HomeworkCorrection[] = [];
  @Output('upload') upload = new EventEmitter<File>();

  corrVisibility: boolean = false;
  corrsToShow: HomeworkCorrection[] = [];
  showedId: number = -1;

  routeQueryParams$: Subscription;

  constructor(private location: Location,
              private route: ActivatedRoute,
              private router: Router,
              private dialog: MatDialog) { }

  ngOnInit(): void {
    this.routeQueryParams$ = this.route.queryParams.subscribe(params => {
      if (params['uploadCorrection']) {
        this.uploadCorrection();
      }
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.homework != null) {
      this.homework = changes.homework.currentValue;
    }

    if (changes.version != null) {
      this.versions = changes.versions.currentValue;
    }

    if (changes.corrections != null) {
      this.corrections = changes.corrections.currentValue;

      this.corrsToShow = [];
      if (this.corrVisibility && this.showedId != -1) {
        this.corrections.forEach(cor => {
          if (cor.versionId == this.showedId) {
            this.corrsToShow.push(cor);
          }
        });
      }
    }
  }


  back() {
    this.location.back();
  }

  showCorrections(version: HomeworkVersion) {
    if (this.showedId == version.id) {
      this.corrVisibility = false;
      this.showedId = -1;
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

  routeUploadCorrection() {
    this.router.navigate([], {queryParams: {uploadCorrection : "true"}});
  }

  uploadCorrection() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        id: 1,
        title: 'Correction',
        versions: this.versions,
        assId: this.route.parent.snapshot.params.idA,
        hwId: this.homework.id
    };

    const dialogRef =  this.dialog.open(UploadCorrectionContComponent, dialogConfig);
    
    dialogRef.afterClosed().subscribe(result => {
      const queryParams = {}
      this.router.navigate([], { queryParams, replaceUrl: true, relativeTo: this.route });
    });
  }

  openVersionImage(version: HomeworkVersion) {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        title: 'VersionText',
        isTeacher: true,
        type: "version",
        assignmentId: this.route.parent.snapshot.params.idA,
        homeworkId: this.homework.id,
        versionId: version.id
    };

    this.dialog.open(ViewImageContComponent, dialogConfig);
  }

  openCorrectionImage(correction: HomeworkCorrection) {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        title: 'CorrectionText',
        isTeacher: true,
        type: "correction",
        assignmentId: this.route.parent.snapshot.params.idA,
        homeworkId: this.homework.id,
        correctionId: correction.id
    };

    this.dialog.open(ViewImageContComponent, dialogConfig);
  }

  ngOnDestroy() {
    this.routeQueryParams$.unsubscribe();
  }

}
