import { Component, OnInit, Output, Input, OnDestroy } from '@angular/core';
import { Team } from 'src/app/models/team.model';
import { VM } from 'src/app/models/vm.model';
import { TeamService } from 'src/app/services/team.service';
import { CourseService } from 'src/app/services/course.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { VMOwners } from 'src/app/models/vm-owners.model';
import { Subscription } from 'rxjs/internal/Subscription';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-team-vms-cont',
  templateUrl: './team-vms-cont.component.html',
  styleUrls: ['./team-vms-cont.component.css']
})
export class TeamVmsContComponent implements OnInit, OnDestroy {
  private route$: Subscription;

  VMs: VMOwners[] = []
  RESOURCES: any;

  constructor(private teamService: TeamService,
              private courseService: CourseService,
              private teacherService: TeacherService,
              private route: ActivatedRoute) { }

  ngOnInit(): void {
    let courseName = this.courseService.currentCourse.getValue().name;

    this.route$ = this.route.params.subscribe(params => {
      this.teamService.getAllVMTeam(courseName, params.idT).subscribe(
        (data) => {
          let vms: VM[] = data;
          let array: VMOwners[] = new Array();

          vms.forEach(vm => {
            this.teacherService.getOwners(courseName, params.idT, vm.id).subscribe(
              (data) => {
                this.VMs = new Array();
                array.push(new VMOwners(vm.id, vm.numVcpu, vm.diskSpace, vm.ram, vm.status, vm.nameVM, vm.timestamp, data))
                array.forEach(vmow => this.VMs.push(vmow));
              }, 
              (error) => {
                window.alert(error.error.message);
              }
            );
          });

          this.teacherService.getResourcesVM(params.idT).subscribe(
            (data) => {
              this.RESOURCES = data;
            },
            (error) => {
              window.alert(error.error.message);
            }
          )
        },
        (error) => {
          window.alert(error.error.message);
        } 
      );
    });
  }

  ngOnDestroy() {
    this.route$.unsubscribe();
  }

}
