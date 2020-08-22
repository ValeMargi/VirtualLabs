import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { VM } from '../../models/vm.model';
import { AuthService } from '../../auth/auth.service';
import { TeamService } from '../../services/team.service';
import { Team } from '../../models/team.model';
import { CourseService } from 'src/app/services/course.service';
import { TeacherService } from 'src/app/services/teacher.service';

@Component({
  selector: 'app-vms-cont',
  templateUrl: './vms-cont.component.html',
  styleUrls: ['./vms-cont.component.css']
})
export class VmsContComponent implements OnInit {
  
  public COURSE_TEAMS: Team[] = []

  constructor(private teamService: TeamService, 
              private courseService: CourseService,
              private teacherService: TeacherService,
              private authService: AuthService) { 
    
  }

  ngOnInit(): void {
    //provvisorio
    this.COURSE_TEAMS.push(new Team(-1, "Gruppo 1", 1, 0, 0, 0, 0, 0));
    this.COURSE_TEAMS.push(new Team(-1, "Gruppo 2", 1, 0, 0, 0, 0, 0));

    this.teamService.getTeamsForCourse(this.courseService.currentCourse.getValue().name).subscribe(
      (data) => {
        this.COURSE_TEAMS = data;
      },
      (error) => {
        console.log("Gruppi non reperiti");
      }
    )
  }

}
