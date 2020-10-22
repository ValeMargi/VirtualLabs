import { Homework } from './../../../models/homework.model';
import { HomeworkVersion } from './../../../models/homework-version.model';
import { Component,OnInit,AfterViewInit, Output, EventEmitter, Input, OnChanges, SimpleChanges} from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { FormControl, Validators } from '@angular/forms';
@Component({
  selector: 'app-add-homework',
  templateUrl: './add-homework.component.html',
  styleUrls: ['./add-homework.component.css']
})
export class AddHomeworkComponent implements OnInit, AfterViewInit, OnChanges {
  Version: HomeworkVersion[];
  selectedPhoto: File;
  
  @Input() querying: boolean;
  @Output('create') create = new EventEmitter<File>();

  form = {
    name : new FormControl('', [Validators.required, Validators.minLength(3)]),
    file : new FormControl('', [Validators.required])
  }
  getErrorMessage() {
    if (this.form.name.hasError('required') || this.form.file.hasError('required')) {
      return 'Campo obbligatorio';
    }
    if(this.form.name.hasError('minlength')){
      return 'Inserire almeno 3 caratteri';
    }
  }

  constructor(private matDialogRef: MatDialogRef<AddHomeworkComponent>) { }
  ngAfterViewInit() {}

  ngOnInit(): void {
    //this.currentDate = new Date();
    //this.oneWeek = new Date(this.currentDate);
    //this.oneWeek.setDate(this.oneWeek.getDate() + 7);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.querying != undefined) {
      this.querying = changes.querying.currentValue;
    }
  }

  close() {
    this.matDialogRef.close();
  }

  addVersion() {
    if (this.selectedPhoto != null) {
      this.create.emit(this.selectedPhoto);
    }
    else {
      window.alert("Inserire foto versione");
    }
  }

  addVersionImage(imageInput) {
    this.selectedPhoto = imageInput.target.files[0];
  }

}
