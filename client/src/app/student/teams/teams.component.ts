import { AuthService } from 'src/app/auth/auth.service';
import { StudentService } from 'src/app/services/student.service';
import { Component, OnInit, Input, AfterViewInit, ViewChild, OnChanges, SimpleChanges, Output, EventEmitter} from '@angular/core';
import { MatTable, MatTableDataSource } from '@angular/material/table'
import{ Team } from '../../models/team.model';
import{ TeamsContComponent } from './teams-cont/teams-cont.component'
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import {MatDialog, MatDialogConfig} from '@angular/material/dialog';
import { RequestTeamDialogContComponent } from './request-team-dialog/request-team-dialog-cont/request-team-dialog-cont.component';
import { Proposal } from 'src/app/models/proposal.model';
import { Student } from 'src/app/models/student.model';
import { strict } from 'assert';

import { Router, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-teams',
  templateUrl: '../teams/teams.component.html',
  styleUrls: ['../teams/teams.component.css']
})
export class TeamsComponent implements AfterViewInit, OnInit, OnChanges {

  @ViewChild('table') table: MatTable<Element>;

  private sort: MatSort;
  private paginator: MatPaginator;

  @ViewChild(MatSort) set matSort(ms: MatSort) {
    this.sort = ms;
    this.setDataSourceTeamAttributes();
    this.setDataSourceProposalsAttributes();
    this.setDataSourceProposalsAcceptedAttributes();
  }

  @ViewChild(MatPaginator) set matPaginator(mp: MatPaginator) {
    this.paginator = mp;
    this.setDataSourceTeamAttributes();
    this.setDataSourceProposalsAttributes();
    this.setDataSourceProposalsAcceptedAttributes();
  }

  //Table Team
  displayedColumnsTeam: string[] = ['id', 'name', 'firstName'];
  dataSourceTeam = new MatTableDataSource<Student>();
  tableTeamVisibility:boolean = true;

  //Table Request
  displayedColumnsRequest: string[] = ['teamName', 'creator', 'students', 'choice'];
  dataSourceProposals = new MatTableDataSource<Proposal>();

  //Table Request Accepted
  displayedColumnsRequestReceive: string[] = ['teamName', 'creator', 'students'];
  dataSourceProposalsReceive = new MatTableDataSource<Proposal>();

  //My proposal
  myProposal: Proposal;



  @Input() team: Team;
  @Input() proposals: Proposal[] = [];
  @Input() members: Student[] = [];
  @Input() querying: boolean;
  @Output('accept') accept = new EventEmitter<string>();
  @Output('refuse') refuse = new EventEmitter<string>();

  lengthProposals;
  lengthProposalsReceive;
  lengthMembers;
  teamName: string;

  propsVisibility: boolean = false;
  proposReceivedVisibility: boolean = false;
  myPropVisibility: boolean = false;
  stateDisabled: boolean = false;

  invited: string[] = [];

  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  routeQueryParams$: Subscription;

  constructor(private dialog: MatDialog,
              private studentService: StudentService,
              private router: Router,
              private route: ActivatedRoute) { }

  ngOnInit(): void {

    this.routeQueryParams$ = this.route.queryParams.subscribe(params => {
      if (params['requestTeam']) {
        this. openRequestDialog();
      }
    });
  }

  routeToRequest() {
    this.router.navigate([], {queryParams: {requestTeam : "true"}});
  }

  openRequestDialog() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.autoFocus = true;
    dialogConfig.disableClose = false;
    dialogConfig.minWidth = "40%";
    dialogConfig.id = "Request Team";

    const dialogRef = this.dialog.open(RequestTeamDialogContComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(result => {
      const queryParams = {}
      this.router.navigate([], { queryParams, replaceUrl: true, relativeTo: this.route });
    });
  }

  ngAfterViewInit(): void {

  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.team != null) {
      this.team = changes.team.currentValue;

      if (this.team != null) {
        this.teamName = this.team.name;
      }
    }

    if (changes.members != null) {
      this.members = changes.members.currentValue;
      this.setTableTeam();
    }

    if (changes.querying != null) {
      this.querying = changes.querying.currentValue;
    }

    if (changes.proposals != null) {
      this.proposals = changes.proposals.currentValue;

      if (this.proposals != null) {
        this.setTableProposals();
      }
      else {
        this.propsVisibility = false;
        this.myPropVisibility = false;
      }

      this.getInvitations();
    }
  }

  getInvitations() {
    if (this.myProposal == null) {
      return '';
    }

    this.invited = [];

    this.myProposal.students.forEach(s => {
      let text: string = '';
      text = text.concat(s.student + " ➥ ");

      if (s.status == "accepted") {
        text = text.concat("Richiesta accettata");
        this.stateDisabled = !this.stateDisabled;
      }
      else if (s.status == "rejected"){
        text = text.concat("Richiesta rifiutata");
        this.stateDisabled = !this.stateDisabled;
      }
      else if (s.status == "pending"){
        text = text.concat("In attesa di risposta");
      }


      this.invited.push(text);
    });

    return this.invited;
  }

  getOthers(s: any) {
    if (s.student == "(Nessun altro partecipante)") {
      return s.student;
    }

    let text: string = '';
    text = text.concat(s.student + " ➥ ");

    if (s.status == "accepted") {
      text = text.concat("Richiesta accettata");
      this.stateDisabled = !this.stateDisabled;
    }
    else if (s.status == "rejected"){
      text = text.concat("Richiesta rifiutata");
      this.stateDisabled = false;
    }
    else if (s.status == "pending"){
      text = text.concat("In attesa di risposta");
    }

    return text;
  }

  setTableProposals() {
    const propsPending: Proposal[] = [];
    const props: Proposal[] = [];
    this.myProposal = null;

    if (this.proposals.length == 0) {
      this.propsVisibility = false;
      this.proposReceivedVisibility = false;
      this.myPropVisibility = false;
      return;
    }

    const student: Student = this.studentService.currentStudent;
    const studentInfo: string = student.name + " " + student.firstName + " (" + student.id + ")";

    this.proposals.forEach(p => {
      if (p.creator == studentInfo) {
        this.myProposal = p;
        this.getInvitations();
      }
      else {
        if (p.students.length == 0) {
          p.students = new Array(1);
          p.students[0] = {student: "(Nessun altro partecipante)"}
        }

        if (p.status == "accepted" || p.status =="rejects") {
          props.push(p);
        }
        else if (p.status =="pending"){
          propsPending.push(p);
        }
      }
    });

    if (this.myProposal != null) {
      this.myPropVisibility = true;
    }
    else {
      this.myPropVisibility = false;
    }

    if (propsPending.length > 0) {
      this.propsVisibility = true;
    }
    else {
      this.propsVisibility = false;
    }

    if (props.length > 0 ) {
      this.proposReceivedVisibility = true;
    }
    else {
      this.proposReceivedVisibility = false;
    }

    if (this.propsVisibility) {
      this.dataSourceProposals = new MatTableDataSource<Proposal>(propsPending);
      this.setDataSourceProposalsAttributes();
      this.lengthProposals = propsPending.length;
    }

    if (this.proposReceivedVisibility) {
      this.dataSourceProposalsReceive = new MatTableDataSource<Proposal>(props);
      this.setDataSourceProposalsAcceptedAttributes();
      this.lengthProposalsReceive = props.length;
    }
  }

  setTableTeam(){
    this.dataSourceTeam = new MatTableDataSource<Student>(this.members);
    this.setDataSourceTeamAttributes();
    this.lengthMembers = this.members.length;
  }

  setDataSourceTeamAttributes() {
    this.dataSourceTeam.paginator = this.paginator;
    this.dataSourceTeam.sort = this.sort;
  }

  setDataSourceProposalsAttributes() {
    //this.dataSourceProposals.paginator = this.paginator;
    this.dataSourceProposals.sort = this.sort;
  }

  setDataSourceProposalsAcceptedAttributes() {
    //this.dataSourceProposalsAccepted.paginator = this.paginator;
    this.dataSourceProposalsReceive.sort = this.sort;
  }

  acceptProposal(token: string) {
    this.accept.emit(token);
  }

  refuseProposal(token: string) {
    this.refuse.emit(token);
  }

  ngOnDestroy() {
    this.routeQueryParams$.unsubscribe();
  }
}
