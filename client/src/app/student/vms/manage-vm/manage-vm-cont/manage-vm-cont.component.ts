import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { VM } from 'src/app/models/vm.model';
import { CourseService } from 'src/app/services/course.service';
import { StudentService } from 'src/app/services/student.service';

@Component({
  selector: 'app-manage-vm-cont',
  templateUrl: './manage-vm-cont.component.html',
  styleUrls: ['./manage-vm-cont.component.css']
})
export class ManageVmContComponent implements OnInit {

  VM: VM;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
            private dialogRef: MatDialogRef<ManageVmContComponent>,
            private studentService: StudentService,
            private courseService: CourseService) { }

  ngOnInit(): void {
    this.VM = this.data.vm;
  }
 
  updateVM(vm: VM) {
    this.studentService.updateVMresources(this.courseService.currentCourse.getValue().name, vm.id, vm).subscribe(
      (data) => {
        this.dialogRef.close();
      },
      (error) => {
        console.log("Impossibile aggiornare le informazioni della VM");
      }
    )
  }


}
