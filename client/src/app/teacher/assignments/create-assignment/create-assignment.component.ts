import { Component, OnInit, AfterViewInit, Output, EventEmitter } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { Assignment } from 'src/app/models/assignment.model';

import { FormGroup, FormBuilder } from '@angular/forms';
import * as moment from 'moment';

@Component({
  selector: 'app-create-assignment',
  templateUrl: './create-assignment.component.html',
  styleUrls: ['./create-assignment.component.css']
})
export class CreateAssignmentComponent implements OnInit, AfterViewInit {
  CreateAssignmentForm: FormGroup;
  selectedPhoto: File;
  currentDate;
  oneWeek;

  @Output('create') create = new EventEmitter<any>();

  constructor(
    private matDialogRef: MatDialogRef<CreateAssignmentComponent>,
    private formBuilder: FormBuilder) {
    }

  ngAfterViewInit() {}

  ngOnInit(): void {
    this.currentDate = new Date();
    this.oneWeek = new Date(this.currentDate);
    this.oneWeek.setDate(this.oneWeek.getDate() + 7);
  }

  close() {
    this.matDialogRef.close();
  }

  addAssImage(imageInput) {
    this.selectedPhoto = imageInput.target.files[0];
  }

  createAss(name: string, release: string, expire: string) {
    if (name == null || name.length == 0) {
      window.alert("Inserire un nome per la consegna");
    }
    else if (this.selectedPhoto == null) {
      window.alert("Inserire un'immagine per la consegna");
    }

    let res = expire.split("-");
    let date = new Date(Number.parseInt(res[0]), Number.parseInt(res[1]) - 1, Number.parseInt(res[2]), 23, 59, 59, 999);
    let assignment = new Assignment(-1, name, release, moment(date).format("YYYY-MM-DD HH:mm:ss.SSS"));

    this.create.emit({assignment: assignment, file: this.selectedPhoto});
  }
}
