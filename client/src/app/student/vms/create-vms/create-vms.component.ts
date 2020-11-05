import { Component, OnInit, AfterViewChecked, AfterViewInit, Output, EventEmitter, Input } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { VM } from 'src/app/models/vm.model';
import { Team } from 'src/app/models/team.model';
@Component({
  selector: 'app-create-vms',
  templateUrl: './create-vms.component.html',
  styleUrls: ['./create-vms.component.css']
})
export class CreateVmsComponent implements OnInit {

  @Output('create') create = new EventEmitter<VM>();

  @Input() team: Team;

  createVmForm: FormGroup;

  constructor(private matDialogRef: MatDialogRef<CreateVmsComponent>,
              private formBuilder: FormBuilder) {

    let maxVcpu = this.team.maxVpcuLeft;
    console.log(maxVcpu);

    this.createVmForm =this.formBuilder.group({
      name: new FormControl('', [Validators.required]),
      vcpu: new FormControl('', [Validators.required, Validators.max(maxVcpu)]),
      ram: new FormControl('', [Validators.required]),
      disk: new FormControl('', [Validators.required])
    },{ validator: this.checkResources});
   }

  ngOnInit(): void {
  }

  close() {
    this.matDialogRef.close();
  }

  checkResources(group: FormGroup){

  }

  createVM(vcpu: number, diskSpace: number, ram: number, name: string) {
    let vm = new VM(-1, vcpu, diskSpace, ram, "", name, "");
    this.create.emit(vm);
  }
}
