import { Component, OnInit, Output, Input } from '@angular/core';
import { Team } from 'src/app/models/team.model';
import { VM } from 'src/app/models/vm.model';
import { TeamService } from 'src/app/services/team.service';
import { CourseService } from 'src/app/services/course.service';
import { TeacherService } from 'src/app/services/teacher.service';

@Component({
  selector: 'app-team-vms-cont',
  templateUrl: './team-vms-cont.component.html',
  styleUrls: ['./team-vms-cont.component.css']
})
export class TeamVmsContComponent implements OnInit {

  @Input() public team: Team;
  @Output() public VMs: VM[] = []

  constructor(private teamService: TeamService,
              private courseService: CourseService,
              private teacherService: TeacherService) { }

  ngOnInit(): void {
    //this.VMs.push(new VM(-1, 2, 100, 2, "off", "VM01", ""));
    this.teamService.getAllVMTeam(this.courseService.currentCourse.getValue().name, this.team.id).subscribe(
      (data) => {
        this.VMs = data;

        
      },
      (error) => {
        console.log("Errore nel recupero delle VM");
      } 
    );
  }

}
