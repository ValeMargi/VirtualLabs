import { Component, OnInit, Input } from '@angular/core';
import { Course } from 'src/app/models/course.model';
import { TeacherService } from 'src/app/services/teacher.service';
import { FormControl, Validators, FormGroup, FormBuilder } from '@angular/forms';

@Component({
  selector: 'app-manage-model',
  templateUrl: './manage-model.component.html',
  styleUrls: ['./manage-model.component.css']
})
export class ManageModelComponent implements OnInit {

  @Input() modelvm: Course;
  ModelVmForm: FormGroup;

  constructor(private teacherService: TeacherService,
    private formBuilder: FormBuilder) { 

      this.ModelVmForm = this.formBuilder.group({
        max_vcpu : new FormControl('', [Validators.required, Validators.min(1),Validators.max(24)]),
        max_disco : new FormControl('', [Validators.required, Validators.min(10),Validators.max(500)]),
        max_ram : new FormControl('', [Validators.required, Validators.min(1),Validators.max(250)]),
        max_vm : new FormControl('', [Validators.required, Validators.min(1),Validators.max(10)]),
        max_vm_active : new FormControl('', [Validators.required, Validators.min(1),Validators.max(10)])
      });
    }

  ngOnInit(): void {
   
  }

  saveModel(maxVcpu: number, maxDisk: number, ram: number, totInstances: number, runningInstances: number) {
    this.modelvm.maxVcpu = maxVcpu;
    this.modelvm.diskSpace = maxDisk;
    this.modelvm.ram = ram;
    this.modelvm.totInstances = totInstances;
    this.modelvm.runningInstances = runningInstances;
    
    this.teacherService.updateModelVM(this.modelvm.name, this.modelvm);
  }

}
