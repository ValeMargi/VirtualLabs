import { Component, OnInit, AfterViewChecked, AfterViewInit, Output, EventEmitter, Input, OnChanges, SimpleChanges } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { FormGroup, FormControl, Validators, FormBuilder, AbstractControl } from '@angular/forms';
import { VM } from 'src/app/models/vm.model';
import { Team } from 'src/app/models/team.model';
@Component({
  selector: 'app-create-vms',
  templateUrl: './create-vms.component.html',
  styleUrls: ['./create-vms.component.css']
})
export class CreateVmsComponent implements OnInit, OnChanges {

  @Input() team: Team;
  @Output('create') create = new EventEmitter<VM>();

  createVmForm: FormGroup;

  constructor(private matDialogRef: MatDialogRef<CreateVmsComponent>,
              private formBuilder: FormBuilder) {

    this.createVmForm = this.formBuilder.group({
      name: new FormControl('', [Validators.required]),
      vcpu: new FormControl('', [Validators.required, Validators.min(1)]),
      ram: new FormControl('', [Validators.required, Validators.min(1)]),
      disk: new FormControl('', [Validators.required, Validators.min(1)])
    });
   }

  ngOnInit(): void {
    
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.team != null) {
      this.team = changes.team.currentValue;

      if (this.team != null) {
        this.createVmForm = this.formBuilder.group({
          name: new FormControl('', [Validators.required]),
          vcpu: new FormControl('', [Validators.required, Validators.min(1), Validators.max(this.team.maxVcpuLeft)]),
          ram: new FormControl('', [Validators.required, Validators.min(1), Validators.max(this.team.ramLeft)]),
          disk: new FormControl('', [Validators.required, Validators.min(1), Validators.max(this.team.diskSpaceLeft)])
        });

        this.createVmForm.patchValue({
          vcpu: this.team.maxVcpuLeft,
          disk: this.team.diskSpaceLeft,
          ram: this.team.ramLeft
        });
      }
    }
  }

  close() {
    this.matDialogRef.close();
  }

  createVM(vcpu: number, diskSpace: number, ram: number, name: string) {
    if (!this.createVmForm.valid) {
      window.alert("Controllare che i dati inseriti siano validi e riprovare");
      return;
    }

    let vm = new VM(-1, vcpu, diskSpace, ram, "", name, "");
    this.create.emit(vm);
  }
}
