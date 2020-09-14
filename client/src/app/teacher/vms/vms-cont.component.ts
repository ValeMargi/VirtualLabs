import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { VM } from '../../models/vm.model';
import { AuthService } from '../../auth/auth.service';
import { TeamService } from '../../services/team.service';
import { Team } from '../../models/team.model';
import { CourseService } from 'src/app/services/course.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { ActivatedRoute } from '@angular/router';

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
              private authService: AuthService,
              private route: ActivatedRoute) { 
    
  }

  ngOnInit(): void {
    //this.route.params.subscribe( params => console.log(params));

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
