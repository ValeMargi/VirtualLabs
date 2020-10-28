import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Student } from 'src/app/models/student.model';
import { VMOwners } from 'src/app/models/vm-owners.model';
import { VM } from 'src/app/models/vm.model';
import { CourseService } from 'src/app/services/course.service';
import { StudentService } from 'src/app/services/student.service';

@Component({
  selector: 'app-manage-vm-cont',
  templateUrl: './manage-vm-cont.component.html',
  styleUrls: ['./manage-vm-cont.component.css']
})
export class ManageVmContComponent implements OnInit {

  VM: VMOwners;
  STUDENTS_IN_TEAM: Student[] = [];

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
            private dialogRef: MatDialogRef<ManageVmContComponent>,
            private studentService: StudentService,
            private courseService: CourseService) { }

  ngOnInit(): void {
    this.VM = this.data.vm;
    this.STUDENTS_IN_TEAM = this.data.members;
  }
 
  updateVM(content: any) {
    const vm: VM = content.vm;
    const members: Student[] = content.members;
    const courseName: string = this.courseService.currentCourse.getValue().name;

    this.studentService.updateVMresources(courseName, vm.id, vm).subscribe(
      (data) => {
        if (members.length > 0) {
          this.studentService.addOwners(courseName, this.VM.id, members.map(m => m.id)).subscribe(
            (data) => {
              if (data) {
                this.dialogRef.close();       
                members.forEach(m => this.VM.owners.push(m));
              }
              else {
                window.alert("Owners non aggiunti, si prega di riprovare");
              }
            },
            (error) => {
              window.alert(error.error.message);
            }
          );
        }
        else {
          this.dialogRef.close();
        }
      },
      (error) => {
        window.alert(error.error.message);
      }
    )
  }

  deleteVM() {
    this.studentService.removeVM(this.courseService.currentCourse.getValue().name, this.VM.id).subscribe(
      (data) => {
        if (data) {
          this.dialogRef.close();
          this.studentService.vmDelete.emit(this.VM);
        }
        else {
          window.alert("VM non eliminata, si prega di riprovare");
        }
      },
      (error) => {
        window.alert(error.error.message);
      }
    );
  }

}
