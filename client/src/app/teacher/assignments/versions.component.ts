import { Component, OnInit, Input } from '@angular/core';
import { HomeworkVersion } from 'src/app/models/homework-version.model';
import { HomeworksComponent } from './homeworks.component';

@Component({
  selector: 'app-versions',
  templateUrl: './versions.component.html',
  styleUrls: ['./versions.component.css']
})
export class VersionsComponent implements OnInit {

  @Input() public versions: HomeworkVersion[] = [];
  @Input() public reviews: HomeworkVersion[] = [];

  constructor(private homeworkComponent: HomeworksComponent) { }

  ngOnInit(): void {
  }

  back() {
    this.homeworkComponent.versionsVisibility = false;
  }

}
