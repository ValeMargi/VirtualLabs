import { Component,OnInit,AfterViewInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { FormGroup, FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-add-homework',
  templateUrl: './add-homework.component.html',
  styleUrls: ['./add-homework.component.css']
})
export class AddHomeworkComponent implements OnInit, AfterViewInit {

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

  currentDate; oneWeek;
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

  addAss() {
    this.matDialogRef.close();
  }


  onFileSelected(event){
    console.log(event);
  }
}
