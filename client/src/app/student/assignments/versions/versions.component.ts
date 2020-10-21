import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { HomeworkVersion } from 'src/app/models/homework-version.model';
import { HomeworkCorrection } from 'src/app/models/homework-correction.model';
import { Homework } from 'src/app/models/homework.model';
import { Location } from '@angular/common';

@Component({
  selector: 'app-versions-student',
  templateUrl: './versions.component.html',
  styleUrls: ['./versions.component.css']
})
export class VersionsComponent implements OnInit, OnChanges {

  @Input() homework: Homework;
  @Input() versions: HomeworkVersion[] = [];
  @Input() corrections: HomeworkCorrection[] = [];

  constructor(private location: Location) { }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.versions != null) {
      this.versions = changes.versions.currentValue;
    }

    if (changes.versions != null) {
      this.corrections = changes.corrections.currentValue;
    }
  }

  uploadVersion(test) {

  }

  back() {
    this.location.back();
  }

}
