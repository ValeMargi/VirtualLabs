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
export class TeamsComponent implements OnInit, OnChanges {

  @ViewChild('table') table: MatTable<Element>;

  private sort: MatSort;
  private paginator: MatPaginator;

  @ViewChild(MatSort) set matSort(ms: MatSort) {
    this.sort = ms;
    this.setDataSourceTeamAttributes();
    this.setDataSourceAcceptedAttributes();
    this.setDataSourcePendingAttributes();
    this.setDataSourceRejectedAttributes();
  }

  @ViewChild(MatPaginator) set matPaginator(mp: MatPaginator) {
    this.paginator = mp;
    this.setDataSourceTeamAttributes();
    //this.setDataSourceProposalsAttributes();
    //this.setDataSourceProposalsAcceptedAttributes();
  }

  //Table Team
  displayedColumnsTeam: string[] = ['id', 'name', 'firstName'];
  dataSourceTeam = new MatTableDataSource<Student>();
  tableTeamVisibility:boolean = true;

  //Tabella Richieste Pending
  displayedColumnsRequest: string[] = ['teamName', 'creator', 'students', 'choice'];
  dataSourcePending = new MatTableDataSource<Proposal>();

  //Tabella richieste accettate
  dataSourceAccepted = new MatTableDataSource<Proposal>();

  //Tabella richieste rifiutate
  //displayedColumnsResponded: string[] = ['teamName', 'creator', 'students', 'status'];
  dataSourceRejected = new MatTableDataSource<Proposal>();

  @Input() team: Team;
  @Input() myProposal: Proposal;
  @Input() propsAccepted: Proposal[] = [];
  @Input() propsPending: Proposal[] = [];
  @Input() propsRejected: Proposal[] = [];
  @Input() members: Student[] = [];
  @Input() querying: boolean;
  @Output('accept') accept = new EventEmitter<string>();
  @Output('refuse') refuse = new EventEmitter<string>();

  lengthPending;
  lengthAccepted;
  lengthRejected;
  lengthMembers;
  teamName: string;

  propsAcceptedVisibility: boolean = false;
  propsRejectedVisibility: boolean = false;
  propsPendingVisibility: boolean = false;
  myPropVisibility: boolean = false;
  doPropVisibility: boolean = false;
  stateDisabled: boolean = false;

  requestVisibility: boolean = false;

  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  routeQueryParams$: Subscription;

  constructor(private router: Router,
              private route: ActivatedRoute) { }

  ngOnInit(): void {
    /*this.routeQueryParams$ = this.route.queryParams.subscribe(params => {
      if (params['requestTeam']) {
        this.openRequestDialog();
      }
    });*/
  }

  showRequestTeam() {
    this.router.navigate(['request'], { relativeTo: this.route });
  }

  onRouterOutletActivate(event) {
    this.requestVisibility = true;
  }

  onRouterOutletDeactivate(event) {
    this.requestVisibility = false;
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.team != null) {
      this.team = changes.team.currentValue;

      if (this.team != null) {
        this.teamName = this.team.name;
        this.doPropVisibility = false;
      }
      else {
        this.doPropVisibility = true;
      }
    }

    if (changes.members != null) {
      this.members = changes.members.currentValue;
      this.setTableTeam();
    }

    if (changes.querying != null) {
      this.querying = changes.querying.currentValue;
    }

    if (changes.myProposal != null) {
      this.myProposal = changes.myProposal.currentValue;

      if (this.myProposal != null) {
        this.setMyProposal();
      }
    }

    if (changes.propsAccepted != null) {
      this.propsAccepted = changes.propsAccepted.currentValue;

      if (this.propsAccepted != null) {
        this.setTablePropsAccepted();
      }
    }

    if (changes.propsPending != null) {
      this.propsPending = changes.propsPending.currentValue;

      if (this.propsPending != null) {
        this.setTablePropsPending();
      }
    }

    if (changes.propsRejected != null) {
      this.propsRejected = changes.propsRejected.currentValue;

      if (this.propsRejected != null) {
        this.setTablePropsRejected();
      }
    }

    this.manageVisibilities();
  }

  //riceve uno studente della proposal, ci aggiunge lo stato 
  getOthers(s: any) {
    if (s.student == "(Nessun altro partecipante)") {
      return s.student;
    }

    let text: string = '';
    text = text.concat(s.student + " ➥ ");

    if (s.status == "accepted") {
      text = text.concat("Richiesta accettata");
    }
    else if (s.status == "rejected") {
      text = text.concat("Richiesta rifiutata");
    }
    else if (s.status == "pending") {
      text = text.concat("In attesa di risposta");
    }

    return text;
  }

  setMyProposal() {
    
  }

  setTablePropsAccepted() {
    this.dataSourceAccepted = new MatTableDataSource<Proposal>(this.propsAccepted);
    this.setDataSourceAcceptedAttributes();
    this.lengthAccepted = this.propsAccepted.length;
  }

  setTablePropsPending() {
    this.dataSourcePending = new MatTableDataSource<Proposal>(this.propsPending);
    this.setDataSourceAcceptedAttributes();
    this.lengthPending = this.propsPending.length;
  }

  setTablePropsRejected() {
    this.dataSourceRejected = new MatTableDataSource<Proposal>(this.propsRejected);
    this.setDataSourceAcceptedAttributes();
    this.lengthRejected = this.propsRejected.length;
  }

  manageVisibilities() {
    if (this.myProposal != null) {
      this.myPropVisibility = true;

      if (this.myProposal.teamStatus == "pending") {
        this.doPropVisibility = false;
      }
      else {
        this.doPropVisibility = true;
      }
    }
    else {
      this.myPropVisibility = false;

      if (this.team == null) {
        this.doPropVisibility = true;
      }
      else {
        this.doPropVisibility = false;
      }
    }

    if (this.propsAccepted.length > 0) {
      this.propsAcceptedVisibility = true;
    }
    else {
      this.propsAcceptedVisibility = false;
    }

    if (this.propsPending.length > 0) {
      this.propsPendingVisibility = true;
    }
    else {
      this.propsPendingVisibility = false;
    }

    if (this.propsRejected.length > 0) {
      this.propsRejectedVisibility = true;
    }
    else {
      this.propsRejectedVisibility = false;
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

  setDataSourceAcceptedAttributes() {
    //this.dataSourceProposals.paginator = this.paginator;
    this.dataSourceAccepted.sort = this.sort;
  }

  setDataSourcePendingAttributes() {
    //this.dataSourceProposalsAccepted.paginator = this.paginator;
    this.dataSourcePending.sort = this.sort;
  }

  setDataSourceRejectedAttributes() {
    //this.dataSourceProposalsAccepted.paginator = this.paginator;
    this.dataSourceRejected.sort = this.sort;
  }

  acceptProposal(token: string) {
    this.accept.emit(token);
  }

  refuseProposal(token: string) {
    this.refuse.emit(token);
  }

  ngOnDestroy() {
    //this.routeQueryParams$.unsubscribe();
  }
}
