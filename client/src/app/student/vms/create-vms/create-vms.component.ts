import { Component, OnInit, AfterViewChecked, AfterViewInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { FormGroup, FormControl, Validators } from '@angular/forms';
@Component({
  selector: 'app-create-vms',
  templateUrl: './create-vms.component.html',
  styleUrls: ['./create-vms.component.css']
})
export class CreateVmsComponent implements OnInit {

  constructor(private matDialogRef: MatDialogRef<CreateVmsComponent>) { }

  form = {
    name : new FormControl('', [Validators.required, Validators.minLength(3)])
  }

  getErrorMessage() {
    if (this.form.name.hasError('required')) {
      return 'Campo obbligatorio';
    }
    if(this.form.name.hasError('minlength')){
      return 'Inserire almeno 3 caratteri';
    }
  }

  ngOnInit(): void {
  }


  close() {
    this.matDialogRef.close();
  }

  createVms() {
    
  }
}
