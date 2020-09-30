import { Component, OnInit, Output, Input } from '@angular/core';
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
export class TeamVmsContComponent implements OnInit {
  private route$: Subscription;

  @Input() public team: Team;
  @Output() public VMs: VMOwners[] = []

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
          console.log("Errore nel recupero delle VM");
        } 
      );
    });
  }

}
