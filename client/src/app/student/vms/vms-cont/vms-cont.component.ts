import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { VM } from '../../../models/vm.model';
import { AuthService } from '../../../auth/auth.service';
import { TeamService } from '../../../services/team.service';
import { Team } from '../../../models/team.model';
import { CourseService } from 'src/app/services/course.service';
import { Student } from 'src/app/models/student.model';
import { StudentService } from 'src/app/services/student.service';
import { VMOwners } from 'src/app/models/vm-owners.model';

@Component({
  selector: 'app-vms-cont',
  templateUrl: './vms-cont.component.html',
  styleUrls: ['./vms-cont.component.css']
})
export class VmsContComponent implements OnInit {

  public VMs: VMOwners[] = [];
  public TEAM: Team;

  constructor(private teamService: TeamService, 
    private courseService: CourseService, 
    private studentService: StudentService) { 
    
  }


  ngOnInit() {
    let courseName = this.courseService.currentCourse.getValue().name;

    this.teamService.getTeamForStudent(courseName, this.studentService.currentStudent.id).subscribe(
      (data) => {
        if (data != null) {
          this.TEAM = data;
          
          this.teamService.getAllVMTeam(courseName, this.TEAM.id).subscribe(
            (data) => {
              let vms: VM[] = data;
              let array: VMOwners[] = new Array();

              vms.forEach(vm => {
                this.studentService.getOwners(courseName, this.TEAM.id, vm.id).subscribe(
                  (data) => {
                    array.push(new VMOwners(vm.id, vm.numVcpu, vm.diskSpace, vm.ram, vm.status, vm.nameVM, vm.timestamp, data))
                    this.VMs = array;
                  }, 
                  (error) => {
                    console.log("Impossibile ottenere gli owners");
                  }
                );
              });
            },
            (error) => {
              console.log("Impossibile reperire le VM per il team")
            }
          );
        }
      },
      (error) => {
        console.log("Impossibile reperire il team dello studente")
      }
    );

    this.studentService.vmCreation.subscribe(
      (data) => {
        if (this.VMs.length == 0) {
          let array: VMOwners[] = new Array();
          array.push(data);
          this.VMs = array;
        }
        else {
          this.VMs.push(data);
        }
      }, 
      (error) => {

      }
    );
  }

  activateVM(vmId: number) {
    this.studentService.activateVM(this.courseService.currentCourse.getValue().name, vmId).subscribe(
      (data) => {
        this.VMs.forEach(vm => {
          if (vm.id == vmId) {
            vm.status = "on";
          }
        })
      },
      (error) => {
        console.log("Impossibile attivare la VM");
      }
    )
  }

  disableVM(vmId: number) {
    this.studentService.disableVM(this.courseService.currentCourse.getValue().name, vmId).subscribe(
      (data) => {
        this.VMs.forEach(vm => {
          if (vm.id == vmId) {
            vm.status = "off";
          }
        })
      },
      (error) => {
        console.log("Impossibile spegnere la VM");
      }
    )
  }
}
