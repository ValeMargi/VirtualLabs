import { Team } from './../../../../models/team.model';
import { TeamService } from 'src/app/services/team.service';
import { CourseService } from './../../../../services/course.service';
import { Component, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-request-team-dialog-cont',
  templateUrl: './request-team-dialog-cont.component.html',
  styleUrls: ['./request-team-dialog-cont.component.css']
})
export class RequestTeamDialogContComponent implements OnInit {

  @Output() public newTeam: Team;

  constructor(private courseService: CourseService,
              private teamService: TeamService) { }

  ngOnInit(): void {

    /*
    this.teamService.proposeTeam(this.courseService.currentCourse.getValue().name, teamMap<String,String>)
    .subscribe(
      (data) => {
        this.newTeam = data;
      }
      (error) =>{
        console.log("Errroe Team")
      }
    );
    */
  }


}
