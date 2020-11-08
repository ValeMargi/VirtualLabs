import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Student } from 'src/app/models/student.model';
import { Team } from 'src/app/models/team.model';
import { VMOwners } from 'src/app/models/vm-owners.model';
import { VM } from 'src/app/models/vm.model';
import { CourseService } from 'src/app/services/course.service';
import { StudentService } from 'src/app/services/student.service';
import { TeamService } from 'src/app/services/team.service';

@Component({
  selector: 'app-manage-vm-cont',
  templateUrl: './manage-vm-cont.component.html',
  styleUrls: ['./manage-vm-cont.component.css']
})
export class ManageVmContComponent implements OnInit {

  VM: VMOwners;
  STUDENTS_IN_TEAM: Student[] = [];
  TEAM: Team;

  updated: boolean = false;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
            private dialogRef: MatDialogRef<ManageVmContComponent>,
            private studentService: StudentService,
            private courseService: CourseService,
            private teamService: TeamService) { }

  ngOnInit(): void {
    this.VM = this.data.vm;
    this.STUDENTS_IN_TEAM = this.data.members;

    this.teamService.getTeamForStudent(this.courseService.currentCourse.getValue().name, this.studentService.currentStudent.id).subscribe(
      (data) => {
        this.TEAM = data;
      },
      (error) => {
        window.alert(error.error.message);
      }
    );
  }
 
  updateVM(content: any) {
    const vm: VM = content.vm;
    const members: Student[] = content.members;
    const courseName: string = this.courseService.currentCourse.getValue().name;

    this.studentService.updateVMresources(courseName, vm.id, vm).subscribe(
      (data) => {
        this.updated = true;
        this.VM.numVcpu = data.numVcpu;
        this.VM.diskSpace = data.diskSpace;
        this.VM.ram = data.ram;

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
