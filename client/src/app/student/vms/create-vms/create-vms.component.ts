import { Component, OnInit, AfterViewChecked, AfterViewInit, Output, EventEmitter } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { VM } from 'src/app/models/vm.model';
@Component({
  selector: 'app-create-vms',
  templateUrl: './create-vms.component.html',
  styleUrls: ['./create-vms.component.css']
})
export class CreateVmsComponent implements OnInit {

  @Output('create') create = new EventEmitter<VM>();

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

  createVM(vcpu: number, diskSpace: number, ram: number, name: string) {
    let vm = new VM(-1, vcpu, diskSpace, ram, "", name, "");
    this.create.emit(vm);
  }
}
