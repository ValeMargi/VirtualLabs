import { Component, OnInit } from '@angular/core';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.css']
})
export class EditProfileComponent implements OnInit {

  constructor(private matDialog: MatDialog, private dialogRef: MatDialogRef<EditProfileComponent>) { }

  changePassVisibility: boolean = false;

  ngOnInit(): void {
  }

  close() {
    this.dialogRef.close();
  }

  toggleChangePass() {
    this.changePassVisibility = !this.changePassVisibility;
  }

}
