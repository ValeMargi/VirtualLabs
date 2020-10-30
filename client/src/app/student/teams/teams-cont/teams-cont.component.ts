import { CourseService } from './../../../services/course.service';
import { TeamService } from './../../../services/team.service';
import { StudentService } from './../../../services/student.service';
import { Component, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { Team } from '../../../models/team.model';
import { Student } from 'src/app/models/student.model';
import { Course } from 'src/app/models/course.model';
import { Proposal } from 'src/app/models/proposal.model';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';
@Component({
  selector: 'app-teams-cont',
  templateUrl: '../teams-cont/teams-cont.component.html',
  styleUrls: ['../teams-cont/teams-cont.component.css']
})
export class TeamsContComponent implements OnInit, OnDestroy {

  private route$: Subscription;

  constructor(private teamService: TeamService,
              private courseService: CourseService,
              private router: Router,
              private route: ActivatedRoute) { }

  TEAM: Team;
  PROPOSALS: Proposal[];
  MEMBERS: Student[] = [];
  QUERYING: boolean = false;

  ngOnInit(): void {
    this.route$ = this.route.params.subscribe(params =>  {
      let courseName: string = params.courses;

      if (courseName == undefined) {
        return;
      }

      this.PROPOSALS = [];
      this.MEMBERS = [];

      this.getTeam(courseName);

      this.teamService.proposal.subscribe(
        (data) => {
          let array: Proposal[] = this.PROPOSALS;
          this.PROPOSALS = new Array();
          array.push(data);

          array.forEach(prop => {
            this.PROPOSALS.push(prop);
          });
        },
        (error) => {

        }
      )
    });
  }

  ngOnDestroy() {
    this.route$.unsubscribe();
  }

  getTeam(courseName: string) {
    let studentId: string = localStorage.getItem('currentId');

    this.teamService.getTeamForStudent(courseName, studentId).subscribe(
      (data) => {
        this.TEAM = data;

        if (this.TEAM != null) {
          this.membersInTeam();
        }
        else {
          this.getProposals(courseName);
        }
      },
      (error) => {
        window.alert(error.error.message);
        const status: number = error.error.status;

        if (status == 404 || status == 403) {
          this.router.navigateByUrl("home");
          this.courseService.courseReload.emit();
        }
      }
    );
  }

  membersInTeam() {
    this.teamService.getMembersTeam(this.TEAM.id).subscribe(
      (data) => {
        this.MEMBERS = data;
      },
      (error) => {
        window.alert(error.error.message);
      }
    );
  }

  getProposals(courseName: string) {
    this.teamService.getProposals(courseName).subscribe(
      (data) => {
        this.PROPOSALS = data;
      },
      (error) => {
        window.alert(error.error.message);
      }
    );
  }

  acceptProposal(token: string) {
    if (this.QUERYING) {
      return;
    }

    this.QUERYING = true;

    this.teamService.confirm(token).subscribe(
      (data) => {
        switch(data) {
          case 0: {
            window.alert("Token non valido");
            this.QUERYING = false;
            break;
          }
          case 1: {
            this.getProposals(this.route.snapshot.params.courses);
            this.QUERYING = false;
            break;
          }
          case 2: {
            this.PROPOSALS = new Array();
            this.getTeam(this.route.snapshot.params.courses);
            this.QUERYING = false;
            break;
          }
        }
      },
      (error) => {
        window.alert(error.error.message);
        this.QUERYING = false;
        const status: number = error.error.status;

        if (status == 404 || status == 403) {
          this.router.navigateByUrl("home");
          this.courseService.courseReload.emit();
        }
      }
    );
  }

  refuseProposal(token: string) {
    if (this.QUERYING) {
      return;
    }

    this.QUERYING = true;

    this.teamService.refuse(token).subscribe(
      (data) => {
        switch(data) {
          case 0: {
            window.alert("Token non valido");
            this.QUERYING = false;
            break;
          }
          case 1: {
            this.getProposals(this.route.snapshot.params.courses);
            this.QUERYING = false;
            break;
          }
        }
      },
      (error) => {
        window.alert(error.error.message);
        this.QUERYING = false;
        const status: number = error.error.status;

        if (status == 404 || status == 403) {
          this.router.navigateByUrl("home");
          this.courseService.courseReload.emit();
        }
      }
    );
  }

}
