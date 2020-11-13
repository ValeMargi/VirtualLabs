import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { Course } from 'src/app/models/course.model';
import { TeacherService } from 'src/app/services/teacher.service';
import { FormControl, Validators, FormGroup, FormBuilder, AbstractControl, ValidatorFn, FormGroupDirective, NgForm } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { CourseService } from 'src/app/services/course.service';
import { Team } from 'src/app/models/team.model';
import { ErrorStateMatcher } from '@angular/material/core';

@Component({
  selector: 'app-manage-model',
  templateUrl: './manage-model.component.html',
  styleUrls: ['./manage-model.component.css']
})
export class ManageModelComponent implements OnInit, OnChanges {

  @Input() modelvm: Course;
  @Input() totRes: any;
  @Output() update: EventEmitter<Course> = new EventEmitter<Course>();

  matcher = new MyErrorStateMatcher();

  ModelVmForm: FormGroup;
  UpVms: boolean = false;
  maxVM: number;
  maxVMActive: number;

  constructor(private formBuilder: FormBuilder,
              private dialogRef: MatDialogRef<ManageModelComponent>){

      this.ModelVmForm = this.formBuilder.group({
        max_vcpu : new FormControl('', [Validators.required, Validators.min(1)]),
        max_disco : new FormControl('', [Validators.required, Validators.min(1)]),
        max_ram : new FormControl('', [Validators.required, Validators.min(1)]),
        max_vm : new FormControl('', [Validators.required, Validators.min(1)]),
        max_vm_active : new FormControl('', [Validators.required, Validators.min(1)])
      }, { validator: this.maxVmValidator });
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
          max_vcpu : new FormControl('', [Validators.required, Validators.min((this.totRes.vcpu) > 0 ? this.totRes.vcpu : 1)]),
          max_disco : new FormControl('', [Validators.required, Validators.min((this.totRes.diskSpace) > 0 ? this.totRes.diskSpace : 1)]),
          max_ram : new FormControl('', [Validators.required, Validators.min((this.totRes.ram) > 0 ? this.totRes.ram : 1)]),
          max_vm : new FormControl('', [Validators.required, Validators.min((this.totRes.total) > 0 ? this.totRes.total : 1)]),
          max_vm_active : new FormControl('', [Validators.required, Validators.min((this.totRes.running) > 0 ? this.totRes.running : 1)])
        }, { validator: this.maxVmValidator });

        this.ModelVmForm.setValue({
          max_vcpu: this.modelvm.maxVcpu,
          max_disco: this.modelvm.diskSpace,
          max_ram: this.modelvm.ram,
          max_vm: this.modelvm.totInstances,
          max_vm_active: this.modelvm.runningInstances
        }
        );
      }
    }
  }

  /*maxVmValidator(form: FormGroup){
    const condition = form.controls.max_vm_active.value > form.controls.max_vm.value;

    console.log("condition: "+ condition);
    console.log("Active: "+form.controls.max_vm_active.value);
    console.log("Vm: "+form.controls.max_vm.value);

    return condition ? null : {ErrorVmActivated: true};
  }*/

  maxVmValidator(group: FormGroup) {
    let max_vm: number = group.controls.max_vm.value;
    let max_vm_active: number = group.controls.max_vm_active.value;

    if(max_vm_active <= max_vm){
      console.log(max_vm_active+" "+max_vm);
      console.log("Corretto");
      return null;
    }else{
      console.log(max_vm_active+" "+max_vm)
      console.log("Sbagliato");
      return { ErrorVmActivated: true };
    }
  }

  requiredValidator(group: FormGroup){
    let max_vm_active: number = group.controls.max_vm_active.value;
    console.log("eseguo il controllo");

    if(max_vm_active != null){
      return {RequiredVmActivated: true};
    }else{
      return null;
    }
  }

  minValidator(group: FormGroup){
    let max_vm_active: number = group.controls.max_vm_active.value;

    if(max_vm_active < ((this.totRes.running) > 0 ? this.totRes.running : 1) ){
      return null;
    }else{
      return {minVmActivated: true};
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

export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl, form: FormGroupDirective | NgForm): boolean {

    const invalidCtrl = !!(control && control.invalid && control.dirty);

    const invalidParent = !!(
      control.parent.touched
      && control.parent.invalid
      && control.parent.hasError('ErrorVmActivated')
      );

    return (invalidParent || invalidCtrl);
  }
}
