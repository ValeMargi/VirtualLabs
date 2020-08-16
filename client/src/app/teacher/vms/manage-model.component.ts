import { Component, OnInit, Input } from '@angular/core';
import { Course } from 'src/app/models/course.model';
import { TeacherService } from 'src/app/services/teacher.service';

@Component({
  selector: 'app-manage-model',
  templateUrl: './manage-model.component.html',
  styleUrls: ['./manage-model.component.css']
})
export class ManageModelComponent implements OnInit {

  @Input() modelvm: Course;

  constructor(private teacherService: TeacherService) { }

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
