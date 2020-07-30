import { Component, OnInit, AfterViewChecked, AfterViewInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-create-assignment',
  templateUrl: './create-assignment.component.html',
  styleUrls: ['./create-assignment.component.css']
})
export class CreateAssignmentComponent implements OnInit, AfterViewInit {

  currentDate;
  oneWeek;

  constructor(private matDialogRef: MatDialogRef<CreateAssignmentComponent>) { }

  ngAfterViewInit() {

  }

  ngOnInit(): void {
    this.currentDate = new Date();
    this.oneWeek = new Date(this.currentDate);
    this.oneWeek.setDate(this.oneWeek.getDate() + 7);
  }

  close() {
    this.matDialogRef.close();
  }

  createAss() {
    
  }

}
