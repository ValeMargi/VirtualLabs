import { Component, OnInit,Input, SimpleChanges } from '@angular/core';
import { HomeworkVersion } from 'src/app/models/homework-version.model';
import { HomeworkCorrection } from 'src/app/models/homework-correction.model';

@Component({
  selector: 'app-versions',
  templateUrl: './versions.component.html',
  styleUrls: ['./versions.component.css']
})
export class VersionsComponent implements OnInit {

  @Input() public versions: HomeworkVersion[] = [];
  @Input() public corrections: HomeworkCorrection[] = [];

  constructor() { }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges) {
    this.versions = changes.versions.currentValue;
    this.corrections = changes.corrections.currentValue;
  }

}
