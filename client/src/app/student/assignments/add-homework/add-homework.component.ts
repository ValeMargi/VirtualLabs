import { Component,OnInit,AfterViewInit, Output, EventEmitter} from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { FormControl, Validators } from '@angular/forms';
import { HomeworkVersion } from '../../../models/homework-version.model'

@Component({
  selector: 'app-add-homework',
  templateUrl: './add-homework.component.html',
  styleUrls: ['./add-homework.component.css']
})
export class AddHomeworkComponent implements OnInit, AfterViewInit {

  //public HOMEWORKS: Homeworks[] = [];
  selectedPhoto: File;
  name;
  date;
  currentDate;
  oneWeek;

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
    console.log("Inserimento Annullato");
    this.matDialogRef.close();
  }

  AddHomework(name: string, date: string) {
    let homework = new HomeworkVersion(-1, name, date);

    if (this.selectedPhoto != null) {
      this.create.emit({homework: homework, file: this.selectedPhoto});
      console.log("Homework aggiunto");
    }
  }

  addHomeworkImage(imageInput) {
    this.selectedPhoto = imageInput.target.files[0];
  }

  onFileSelected(event){
    console.log(event);
  }
}
