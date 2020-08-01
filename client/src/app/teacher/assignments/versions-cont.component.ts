import { Component, OnInit, Output, Input } from '@angular/core';
import { HomeworkVersion } from 'src/app/models/homework-version.model';
import { Student } from 'src/app/models/student.model';
import { Homework } from 'src/app/models/homework.model';

@Component({
  selector: 'app-versions-cont',
  templateUrl: './versions-cont.component.html',
  styleUrls: ['./versions-cont.component.css']
})
export class VersionsContComponent implements OnInit {

  @Input() public homework: Homework;
  @Output() public VERSIONS: HomeworkVersion[] = [];
  @Output() public REVIEWS: HomeworkVersion[] = [];

  constructor() { }

  ngOnInit(): void {
    this.VERSIONS.push(new HomeworkVersion(new Student("s268746", "Carlo", "Verdone", ""), 1, "timestamp1"));
    this.VERSIONS.push(new HomeworkVersion(new Student("s268746", "Carlo", "Verdone", ""), 2, "timestamp2"));
    this.VERSIONS.push(new HomeworkVersion(new Student("s268746", "Carlo", "Verdone", ""), 2, ""));
    this.REVIEWS.push(new HomeworkVersion(new Student("s268746", "Carlo", "Verdone", ""), 2, ""));
    this.REVIEWS.push(new HomeworkVersion(new Student("s268746", "Carlo", "Verdone", ""), 2, ""));
    this.REVIEWS.push(new HomeworkVersion(new Student("s268746", "Carlo", "Verdone", ""), 3, "timestamp3"))
  }

}
