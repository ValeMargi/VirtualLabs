import { TeamService } from './../../../../services/team.service';
import { Component, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { Team } from 'src/app/models/team.model';
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

   TEAM: Team;


  constructor(private matDialogRef: MatDialogRef<CreateVmsComponent>,
    private studentService: StudentService,
    private courseService: CourseService,
    private teamService: TeamService) { }

  ngOnInit(): void {
    this.teamService.getTeamForStudent(this.courseService.currentCourse.getValue().name, this.studentService.currentStudent.id).subscribe(
      (data) => {
        this.TEAM = data;

        if (this.TEAM != null && (this.TEAM.maxVcpuLeft == 0 || this.TEAM.diskSpaceLeft == 0 || this.TEAM.ramLeft == 0)) {
          window.alert("Non ci sono più risorse a sufficienza per creare una nuova macchina virtuale");
          this.matDialogRef.close();
          return;
        }
      },
      (error) => {
        window.alert(error.error.message);
      }
    );
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
