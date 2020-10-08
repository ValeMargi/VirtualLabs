import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { HomeworkVersion } from 'src/app/models/homework-version.model';
import { HomeworksComponent } from '../homework/homework.component';
import { HomeworkCorrection } from 'src/app/models/homework-correction.model';

@Component({
  selector: 'app-versions',
  templateUrl: './versions.component.html',
  styleUrls: ['./versions.component.css']
})
export class VersionsComponent implements OnInit {

  @Input() public versions: HomeworkVersion[] = [];
  @Input() public corrections: HomeworkCorrection[] = [];

  constructor(private homeworkComponent: HomeworksComponent) { }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges) {
    this.versions = changes.versions.currentValue;
    this.corrections = changes.corrections.currentValue;
  }

  back() {
    this.homeworkComponent.versionsVisibility = false;
  }

}
