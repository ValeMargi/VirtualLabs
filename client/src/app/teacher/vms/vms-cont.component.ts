import { Component, OnInit, Output, EventEmitter, OnDestroy } from '@angular/core';
import { VM } from '../../models/vm.model';
import { AuthService } from '../../auth/auth.service';
import { TeamService } from '../../services/team.service';
import { Team } from '../../models/team.model';
import { CourseService } from 'src/app/services/course.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-vms-cont',
  templateUrl: './vms-cont.component.html',
  styleUrls: ['./vms-cont.component.css']
})
export class VmsContComponent implements OnInit, OnDestroy {
  
  public COURSE_TEAMS: Team[] = []

  private route$: Subscription

  constructor(private teamService: TeamService, 
              private router: Router,
              private route: ActivatedRoute) { 
    
  }

  ngOnInit(): void {
    this.route$ = this.route.params.subscribe(params => {
      let courseName = params.courses;

      if (courseName == undefined) {
        return;
      }

      this.teamService.getTeamsForCourse(courseName).subscribe(
        (data) => {
          this.COURSE_TEAMS = data;
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
  }

  ngOnDestroy() {
    this.route$.unsubscribe();
  }

}
