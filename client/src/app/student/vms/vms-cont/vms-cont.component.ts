import { Component, OnInit, Output, EventEmitter, OnDestroy } from '@angular/core';
import { VM } from '../../../models/vm.model';
import { AuthService } from '../../../auth/auth.service';
import { TeamService } from '../../../services/team.service';
import { Team } from '../../../models/team.model';
import { CourseService } from 'src/app/services/course.service';
import { Student } from 'src/app/models/student.model';
import { StudentService } from 'src/app/services/student.service';
import { VMOwners } from 'src/app/models/vm-owners.model';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-vms-cont',
  templateUrl: './vms-cont.component.html',
  styleUrls: ['./vms-cont.component.css']
})
export class VmsContComponent implements OnInit, OnDestroy {

  VMs: VMOwners[] = [];
  TEAM: Team;
  MEMBERS: Student[] = [];

  private route$: Subscription

  constructor(private teamService: TeamService, 
    private courseService: CourseService, 
    private studentService: StudentService,
    private router: Router,
    private route: ActivatedRoute) { 
    
  }


  ngOnInit() {
    let studentId = localStorage.getItem('currentId');

    this.route$ = this.route.params.subscribe(params => {
      let courseName = params.courses;

      if (courseName == undefined) {
        return;
      }

      this.teamService.getTeamForStudent(courseName, studentId).subscribe(
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
                      this.VMs = new Array();
                      array.push(new VMOwners(vm.id, vm.numVcpu, vm.diskSpace, vm.ram, vm.status, vm.nameVM, vm.timestamp, data));
                      array.forEach(vmow => this.VMs.push(vmow));
                    }, 
                    (error) => {
                      window.alert(error.error.message);
                    }
                  );
                });
              },
              (error) => {
                window.alert(error.error.message);
              }
            );

            this.teamService.getMembersTeam(this.TEAM.id).subscribe(
              (data) => {
                this.MEMBERS = data;
              },
              (error) => {
                window.alert(error.error.message);
              }
            )
          }
        },
        (error) => {
          window.alert(error.error.message);
          const status: number = error.error.status;

          if (status == 404 || status == 403) {
            this.router.navigateByUrl("home");
          }
        }
      );
    });

    this.studentService.vmCreation.subscribe(
      (data) => {
        let array: VMOwners[] = this.VMs;
        this.VMs = new Array();
        array.push(data);

        array.forEach(vm => {
          this.VMs.push(vm);
        });
      }, 
      (error) => {

      }
    );

    this.studentService.vmDelete.subscribe(
      (data) => {
        let array: VMOwners[] = this.VMs;
        this.VMs = new Array();

        array.forEach(vm => {
          if (vm.id != data.id) {
            this.VMs.push(vm);
          }
        });
      }, 
      (error) => {

      }
    );
  }

  ngOnDestroy() {
    this.route$.unsubscribe();
  }

  activateVM(vmId: number) {
    this.studentService.activateVM(this.courseService.currentCourse.getValue().name, vmId).subscribe(
      (data) => {
        if (data) {
          this.VMs.forEach(vm => {
            if (vm.id == vmId) {
              vm.status = "on";
            }
          });
        }
      },
      (error) => {
        window.alert("Impossibile attivare la VM");
      }
    )
  }

  disableVM(vmId: number) {
    this.studentService.disableVM(this.courseService.currentCourse.getValue().name, vmId).subscribe(
      (data) => {
        if (data) {
          this.VMs.forEach(vm => {
            if (vm.id == vmId) {
              vm.status = "off";
            }
          });
        }
      },
      (error) => {
        window.alert("Impossibile spegnere la VM");
      }
    )
  }
}
