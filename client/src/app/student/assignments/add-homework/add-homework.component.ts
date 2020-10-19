import { Homework } from './../../../models/homework.model';
import { HomeworkVersion } from './../../../models/homework-version.model';
import { Component,OnInit,AfterViewInit, Output, EventEmitter} from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { FormControl, Validators } from '@angular/forms';
@Component({
  selector: 'app-add-homework',
  templateUrl: './add-homework.component.html',
  styleUrls: ['./add-homework.component.css']
})
export class AddHomeworkComponent implements OnInit, AfterViewInit {
  Version: HomeworkVersion[];
  selectedPhoto: File;
  currentDate;
  oneWeek;
  public assId: number;

  @Output('create') create = new EventEmitter<any>();

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
    this.currentDate = new Date();
    this.oneWeek = new Date(this.currentDate);
    this.oneWeek.setDate(this.oneWeek.getDate() + 7);
  }

  close() {
    this.matDialogRef.close();
  }

  AddVersion(name: string, date: string, assId:number) {
    let homework = new Homework(-1,"NULL",true,"");
    let version = new HomeworkVersion(-1, name, date);

    this.assId = assId;

    if (this.selectedPhoto != null) {
      this.create.emit({homework:homework,version: version, file: this.selectedPhoto});
      console.log("Homework aggiunto");
    }
  }

  addVersionImage(imageInput) {
    this.selectedPhoto = imageInput.target.files[0];
  }

}
