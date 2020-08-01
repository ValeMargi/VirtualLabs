import { Component, OnInit, Output, Input } from '@angular/core';
import { Homework } from 'src/app/models/homework.model';
import { Assignment } from 'src/app/models/assignment.model';

@Component({
  selector: 'app-homeworks-cont',
  templateUrl: './homeworks-cont.component.html',
  styleUrls: ['./homeworks-cont.component.css']
})
export class HomeworksContComponent implements OnInit {

  @Input() public assignment: Assignment[];
  @Output() public HOMEWORKS: Homework[] = [];

  constructor() { }

  ngOnInit(): void {
    this.HOMEWORKS.push(new Homework("primo", "new", false, ""));
  }

}
