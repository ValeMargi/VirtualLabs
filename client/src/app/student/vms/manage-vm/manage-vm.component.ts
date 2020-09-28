import { Component, Input, Output, OnInit, EventEmitter } from '@angular/core';
import { FormControl, Validators, FormGroup, FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { VM } from 'src/app/models/vm.model';

@Component({
  selector: 'app-manage-vm',
  templateUrl: './manage-vm.component.html',
  styleUrls: ['./manage-vm.component.css']
})
export class ManageVmComponent implements OnInit {

  @Input() vm: VM;
  @Output('update') update: EventEmitter<VM> = new EventEmitter<VM>();

  ModelVmForm: FormGroup;

  constructor(private formBuilder: FormBuilder,
              private dialogRef: MatDialogRef<ManageVmComponent>) { }

  ngOnInit(): void {
    this.ModelVmForm = this.formBuilder.group({
      name : new FormControl('', [Validators.required]),
      vcpu : new FormControl('', [Validators.required, Validators.min(1),Validators.max(24)]),
      disk : new FormControl('', [Validators.required, Validators.min(10),Validators.max(500)]),
      ram : new FormControl('', [Validators.required, Validators.min(1),Validators.max(250)])
    });
  
    this.ModelVmForm.setValue({
      name: this.vm.nameVM,
      vcpu: this.vm.numVcpu,
      disk: this.vm.diskSpace,
      ram: this.vm.ram
    });
  }

  editVM(name: string, vcpu: number, disk: number, ram: number) {
    this.vm.nameVM = name;
    this.vm.numVcpu = Number(vcpu);
    this.vm.diskSpace = Number(disk);
    this.vm.ram = Number(ram);

    this.update.emit(this.vm);
  }

  close() {
    this.dialogRef.close();
  }
}
