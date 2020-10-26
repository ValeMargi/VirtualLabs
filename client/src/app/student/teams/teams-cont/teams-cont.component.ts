import { CourseService } from './../../../services/course.service';
import { TeamService } from './../../../services/team.service';
import { StudentService } from './../../../services/student.service';
import { Component, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { Team } from '../../../models/team.model';
import { Student } from 'src/app/models/student.model';
import { Course } from 'src/app/models/course.model';
import { Proposal } from 'src/app/models/proposal.model';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';
@Component({
  selector: 'app-teams-cont',
  templateUrl: '../teams-cont/teams-cont.component.html',
  styleUrls: ['../teams-cont/teams-cont.component.css']
})
export class TeamsContComponent implements OnInit, OnDestroy {

  private route$: Subscription;

  constructor(private studentService: StudentService,
              private teamService: TeamService,
              private courseService: CourseService,
              private route: ActivatedRoute) { }

  @Output() TEAM: Team;
  @Output() PROPOSALS: Proposal[];
  @Output() MEMBERS: Student[] = [];

  ngOnInit(): void {
    this.route$ = this.route.params.subscribe(params =>  {
      let courseName: string = params.courses;
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
      (data) =>{
        this.TEAM = data;

        if (this.TEAM != null) {
          this.membersInTeam();
        }
        else {
          this.getProposals();
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
        window.alert("Errore caricamento membri");
      }
    );
  }

  getProposals() {
    this.teamService.getProposals(this.courseService.currentCourse.getValue().name).subscribe(
      (data) => {
        this.PROPOSALS = data;
      },
      (error) => {
        window.alert("Errore nel caricare le proposte per lo studente");
      }
    );
  }

  acceptProposal(token: string) {
    this.teamService.confirm(token).subscribe(
      (data) => {
        switch(data) {
          case 0: {
            window.alert("Token non valido");
            break;
          }
          case 1: {
            this.getProposals();
            break;
          }
          case 2: {
            this.PROPOSALS = new Array();
            this.getTeam(this.route.snapshot.params.courses);
            break;
          }
        }
      },
      (error) => {
        window.alert("Errore accettazione richiesta");
      }
    );
  }

  refuseProposal(token: string) {
    this.teamService.refuse(token).subscribe(
      (data) => {
        switch(data) {
          case 0: {
            window.alert("Token non valido");
            break;
          }
          case 1: {
            this.getProposals();
            break;
          }
        }
      },
      (error) => {
        window.alert("Errore rifiuto richiesta");
      }
    );
  }

}
