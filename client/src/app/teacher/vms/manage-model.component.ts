import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { Course } from 'src/app/models/course.model';
import { TeacherService } from 'src/app/services/teacher.service';
import { FormControl, Validators, FormGroup, FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { CourseService } from 'src/app/services/course.service';
import { Team } from 'src/app/models/team.model';

@Component({
  selector: 'app-manage-model',
  templateUrl: './manage-model.component.html',
  styleUrls: ['./manage-model.component.css']
})
export class ManageModelComponent implements OnInit, OnChanges {

  @Input() modelvm: Course;
  @Input() totRes: any;
  @Output() update: EventEmitter<Course> = new EventEmitter<Course>();

  ModelVmForm: FormGroup;

  constructor(private formBuilder: FormBuilder,
              private dialogRef: MatDialogRef<ManageModelComponent>
              ) {
      this.ModelVmForm = this.formBuilder.group({
        max_vcpu : new FormControl('', [Validators.required, Validators.min(1)]),
        max_disco : new FormControl('', [Validators.required, Validators.min(1)]),
        max_ram : new FormControl('', [Validators.required, Validators.min(1)]),
        max_vm : new FormControl('', [Validators.required, Validators.min(1)]),
        max_vm_active : new FormControl('', [Validators.required, Validators.min(1)])
      });
    }

  ngOnInit(): void {

  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.modelvm != null) {
      this.modelvm = changes.modelvm.currentValue;
    }

    if (changes.totRes != null) {
      this.totRes = changes.totRes.currentValue;

      if (this.totRes != null) {
        this.ModelVmForm = this.formBuilder.group({
          max_vcpu : new FormControl('', [Validators.required, Validators.min(this.totRes.vcpu)]),
          max_disco : new FormControl('', [Validators.required, Validators.min(this.totRes.diskSpace)]),
          max_ram : new FormControl('', [Validators.required, Validators.min(this.totRes.ram)]),
          max_vm : new FormControl('', [Validators.required, Validators.min(this.totRes.total)]),
          max_vm_active : new FormControl('', [Validators.required, Validators.min(this.totRes.running)])
        });

        this.ModelVmForm.setValue({
          max_vcpu: this.modelvm.maxVcpu,
          max_disco: this.modelvm.diskSpace,
          max_ram: this.modelvm.ram,
          max_vm: this.modelvm.totInstances,
          max_vm_active: this.modelvm.runningInstances
        });
      }
    }
  }

  saveModel(maxVcpu: number, maxDisk: number, ram: number, totInstances: number, runningInstances: number) {
    if (!this.ModelVmForm.valid) {
      window.alert("Controllare che i dati inseriti rispettino tutti i vincoli e riprovare");
      return;
    }

    this.modelvm.maxVcpu = Number(maxVcpu);
    this.modelvm.diskSpace = Number(maxDisk);
    this.modelvm.ram = Number(ram);
    this.modelvm.totInstances = Number(totInstances);
    this.modelvm.runningInstances = Number(runningInstances);

    this.update.emit(this.modelvm);
  }

  close() {
    this.dialogRef.close();
  }

}
