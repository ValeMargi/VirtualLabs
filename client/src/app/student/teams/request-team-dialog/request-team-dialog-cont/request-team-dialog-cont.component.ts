import { Team } from './../../../../models/team.model';
import { TeamService } from 'src/app/services/team.service';
import { CourseService } from './../../../../services/course.service';
import { Component, OnInit, Output } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-request-team-dialog-cont',
  templateUrl: './request-team-dialog-cont.component.html',
  styleUrls: ['./request-team-dialog-cont.component.css']
})
export class RequestTeamDialogContComponent implements OnInit {
  constructor(private courseService: CourseService,
              private teamService: TeamService,
              private matDialogRef: MatDialogRef<RequestTeamDialogContComponent>) { }

  ngOnInit(): void {

  }

  proposeTeam(content: any) {
    let teamName: string = content.teamName;
    let timeout: string = content.timeout;
    let membersId: string[] = content.membersId;

    this.teamService.proposeTeam(this.courseService.currentCourse.getValue().name, teamName, timeout, membersId).subscribe(
      (data) => {
        //this.newTeam = data;
        console.log(data)
        this.matDialogRef.close();
      },
      (error) =>{
        console.log(error);
        window.alert(error.error.message);
      }
    );
  }


}
