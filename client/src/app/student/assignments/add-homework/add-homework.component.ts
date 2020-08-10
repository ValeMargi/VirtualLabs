import { Component,OnInit,AfterViewInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-add-homework',
  templateUrl: './add-homework.component.html',
  styleUrls: ['./add-homework.component.css']
})
export class AddHomeworkComponent implements OnInit, AfterViewInit {

  currentDate; oneWeek;
  constructor(private matDialogRef: MatDialogRef<AddHomeworkComponent>) { }


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

  addAss() {
  
  }


  onFileSelected(event){
    console.log(event);
  }
}
