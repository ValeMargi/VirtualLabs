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
import { min } from 'rxjs/operators';
@Component({
  selector: 'app-teams-cont',
  templateUrl: '../teams-cont/teams-cont.component.html',
  styleUrls: ['../teams-cont/teams-cont.component.css']
})
export class TeamsContComponent implements OnInit, OnDestroy {

  private route$: Subscription;

  constructor(private teamService: TeamService,
              private courseService: CourseService,
              private studentService: StudentService,
              private router: Router,
              private route: ActivatedRoute) { }

  TEAM: Team;
  MY_PROPOSALS: Proposal[] = [];
  PROPS_ACCEPTED: Proposal[] = [];
  PROPS_PENDING: Proposal[] = [];
  PROPS_REJECTED: Proposal[] = [];
  MEMBERS: Student[] = [];
  QUERYING: boolean = false;

  ngOnInit(): void {
    this.route$ = this.route.params.subscribe(params =>  {
      let courseName: string = params.courses;

      if (courseName == undefined) {
        return;
      }

      this.MY_PROPOSALS = new Array();
      this.PROPS_ACCEPTED = new Array();
      this.PROPS_PENDING = new Array();
      this.PROPS_REJECTED = new Array();
      this.MEMBERS = new Array();
      this.getTeam(courseName);

      this.teamService.proposal.subscribe(
        (data) => {
          if (data.students.length == 0) {
            //solo 1 partecipante, team creato
            this.MY_PROPOSALS = new Array();
            this.PROPS_ACCEPTED = new Array();
            this.PROPS_PENDING = new Array();
            this.PROPS_REJECTED = new Array();
            this.getTeam(this.route.snapshot.params.courses);
          }
          else {
            //aggiornamento proposals
            this.getProposals(courseName);
          }
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
          //se c'è un team, ne carico i membri...
          this.membersInTeam();
        }
        else {
          //...altrimenti carico eventuali proposte
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
        const student: Student = this.studentService.currentStudent;
        const studentInfo: string = student.name + " " + student.firstName + " (" + student.id + ")"; //formato stringa studente del JSON 

        let mine: Proposal[] = new Array();
        let accepted: Proposal[] = new Array();
        let pending: Proposal[] = new Array();
        let rejected: Proposal[] = new Array();

        //per ciascuna proposal, si discrimina lo stato e si mette nell'array corrsipondente
        data.forEach(p => {
          if (p.creator == studentInfo) {
            //il creatore sono io
            mine.push(p);
          }
          else {
            if (p.students.length == 0) {
              //non ci sono altri studenti oltre a me
              p.students = new Array(1);
              p.students[0] = {student: "(Nessun altro partecipante)"}
            }

            if (p.status == "accepted") {
              accepted.push(p);
            }
            else if (p.status =="rejected") {
              rejected.push(p);
            }
            else if (p.status =="pending") {
              pending.push(p);
            }
          }
        });

        this.MY_PROPOSALS = mine;
        this.PROPS_ACCEPTED = accepted;
        this.PROPS_PENDING = pending;
        this.PROPS_REJECTED = rejected;

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
            //errore token
            window.alert("Token non valido");
            this.QUERYING = false;
            break;
          }
          case 1: {
            //richiesta accettata, in attesa
            this.getProposals(this.route.snapshot.params.courses);  //ricarico le proposte
            this.QUERYING = false;
            break;
          }
          case 2: {
            //team creato, si cancellano le proposte...
            this.MY_PROPOSALS = new Array();
            this.PROPS_ACCEPTED = new Array();
            this.PROPS_PENDING = new Array();
            this.PROPS_REJECTED = new Array();
            this.getTeam(this.route.snapshot.params.courses); //...e si carica il nuovo team
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
            //errore token
            window.alert("Token non valido");
            this.QUERYING = false;
            break;
          }
          case 1: {
            //richiesta rifiutata
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
