import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { VMOwners } from 'src/app/models/vm-owners.model';
import { VM } from 'src/app/models/vm.model';
import { CourseService } from 'src/app/services/course.service';
import { StudentService } from 'src/app/services/student.service';
import { CreateVmsComponent } from '../create-vms.component';

@Component({
  selector: 'app-create-vms-cont',
  templateUrl: './create-vms-cont.component.html',
  styleUrls: ['./create-vms-cont.component.css']
})
export class CreateVmsContComponent implements OnInit {

  constructor(private matDialogRef: MatDialogRef<CreateVmsComponent>, 
    private studentService: StudentService,
    private courseService: CourseService) { }

  ngOnInit(): void {
  }

  createVM(vm: VM) {
    this.studentService.addVM(this.courseService.currentCourse.getValue().name, vm).subscribe(
      (data) => {
        this.matDialogRef.close();
        this.studentService.vmCreation.emit(new VMOwners(data.id, data.numVcpu, data.diskSpace, data.ram, data.status, data.nameVM, data.timestamp, [this.studentService.currentStudent]));
      }, 
      (error) => {
        window.alert(error.error.message);
      }
    );
  }

}
