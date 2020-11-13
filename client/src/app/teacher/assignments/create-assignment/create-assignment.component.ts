import { Component, OnInit, AfterViewInit, Output, EventEmitter, Input } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { Assignment } from 'src/app/models/assignment.model';

import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import * as moment from 'moment';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-create-assignment',
  templateUrl: './create-assignment.component.html',
  styleUrls: ['./create-assignment.component.css']
})
export class CreateAssignmentComponent implements OnInit, AfterViewInit {
  CreateAssignmentForm: FormGroup;
  selectedPhoto: File;

  dateTimeout: Date;
  minDate: Date;
  dateControl = new FormControl(new Date());

  @Input() querying: boolean;
  @Output('create') create = new EventEmitter<any>();

  constructor(private matDialogRef: MatDialogRef<CreateAssignmentComponent>,
              private formBuilder: FormBuilder) {

      const currentDate = new Date();
      const oneWeek = new Date(currentDate);
      oneWeek.setDate(oneWeek.getDate() + 7);
      this.minDate = new Date(currentDate);

      this.CreateAssignmentForm = this.formBuilder.group({
        name : new FormControl('', [Validators.required]),
        release: new FormControl(moment(currentDate).format("YYYY-MM-DD"), [Validators.required]),
        date : new FormControl(oneWeek, [Validators.required])
      });
    }

  ngAfterViewInit() {}

  ngOnInit(): void {

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
      return;
    }
    else if (this.selectedPhoto == null) {
      window.alert("Inserire un'immagine per la consegna");
      return;
    }

    let res = expire.split("-");
    let date = new Date(Number.parseInt(res[0]), Number.parseInt(res[1]) - 1, Number.parseInt(res[2]), 23, 59, 59, 999);
    let assignment = new Assignment(-1, name, release, moment(date).format("YYYY-MM-DD HH:mm:ss.SSS"));

    this.create.emit({assignment: assignment, file: this.selectedPhoto});
  }
}
