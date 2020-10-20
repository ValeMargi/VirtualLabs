import { Component, OnInit, Input, OnChanges, SimpleChanges, Output, EventEmitter } from '@angular/core';
import { HomeworkVersion } from 'src/app/models/homework-version.model';
import { HomeworksComponent } from './homeworks.component';
import { HomeworkCorrection } from 'src/app/models/homework-correction.model';
import { Location } from '@angular/common';
import { Homework } from 'src/app/models/homework.model';

@Component({
  selector: 'app-versions-teacher',
  templateUrl: './versions.component.html',
  styleUrls: ['./versions.component.css']
})
export class VersionsComponent implements OnInit, OnChanges {

  @Input() homework: Homework;
  @Input() versions: HomeworkVersion[] = [];
  @Input() corrections: HomeworkCorrection[] = [];
  @Output('upload') upload = new EventEmitter<File>();

  private selectedPhoto: File;

  constructor(private homeworkComponent: HomeworksComponent,
              private location: Location) { }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.homework != null) {
      this.homework = changes.homework.currentValue;
      console.log(this.homework)
    }

    if (changes.version != null) {
      this.versions = changes.versions.currentValue;
    }

    if (changes.corrections != null) {
      this.corrections = changes.corrections.currentValue;
    }
  }

  back() {
    this.homeworkComponent.versionsVisibility = false;
    this.location.back();
  }

  uploadCorrection(file) {
    this.selectedPhoto = file.target.files[0];
    this.upload.emit(this.selectedPhoto);
  }

}
