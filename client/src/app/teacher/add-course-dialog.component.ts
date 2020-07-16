import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-add-course-dialog',
  templateUrl: './add-course-dialog.component.html',
  styleUrls: ['./add-course-dialog.component.css']
})
export class AddCourseDialogComponent implements OnInit {

  constructor(public matDialog: MatDialog, 
      private dialogRef: MatDialogRef<AddCourseDialogComponent>) { }

  ngOnInit(): void {
  }

  close() {
    this.dialogRef.close();
  }

  addCourse() {
    
  }

}
