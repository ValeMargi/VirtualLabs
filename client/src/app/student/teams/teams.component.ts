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

@Component({
  selector: 'app-teams',
  templateUrl: '../teams/teams.component.html',
  styleUrls: ['../teams/teams.component.css']
})
export class TeamsComponent implements AfterViewInit, OnInit, OnChanges {

  @ViewChild('table') table: MatTable<Element>;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  //Table Team
  displayedColumnsTeam: string[] = ['id', 'name', 'firstName'];
  dataSourceTeam = new MatTableDataSource<Student>();
  tableTeamVisibility:boolean = true;

  //Table Request
  displayedColumnsRequest: string[] = ['teamName', 'creator', 'students', 'choice'];
  dataSourceProposals = new MatTableDataSource<Proposal>();
  tableRequestVisibility:boolean = true;
  myProposal: Proposal;

  @Input() public team: Team;
  @Input() public proposals: Proposal[] = [];
  @Input() public members: Student[] = [];

  @Output('accept') accept = new EventEmitter<string>();
  @Output('refuse') refuse = new EventEmitter<string>();


  lengthProposals: number = 0;
  lengthMembers: number = 0;
  teamName: string;

  propsVisibility: boolean = false;
  myPropVisibility: boolean = false;

  invited: string[] = [];

  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  constructor(private dialog: MatDialog) { }

  openRequestDialog() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.autoFocus = true;
    dialogConfig.disableClose = false;
    dialogConfig.minWidth = "40%";
    dialogConfig.id = "Request Team";

    const dialogRef = this.dialog.open(RequestTeamDialogContComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(result => {
      
    });
  }

  ngAfterViewInit(): void {

  }

  ngOnInit(): void {

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

      if (s.status == true) {
        text = text.concat("Richiesta accettata");
      }
      else {
        text = text.concat("In attesa di risposta");
      }

      this.invited.push(text);
    });

    return this.invited;
  }

  setTableProposals() {
    const props: Proposal[] = [];
    
    if (this.proposals.length == 0) {
      this.propsVisibility = false;
      this.myPropVisibility = false;
    }
    else if (this.proposals.length == 1) {
      if (this.proposals[0].status) {
        this.myPropVisibility = true;
        this.propsVisibility = false;
      }
      else {
        this.myPropVisibility = false;
        this.propsVisibility = true;
      }
    }

    this.proposals.forEach(p => {
      if (p.status) {
        this.myProposal = p;
        this.myPropVisibility = true;
        this.getInvitations();
      }
      else {
        if (p.students.length == 0) {
          p.students = new Array(1);
          p.students[0] = {student: "(Nessun altro partecipante)"}
        }

        props.push(p);
        this.propsVisibility = true;
      }
    });

    this.dataSourceProposals = new MatTableDataSource<Proposal>(props);
    this.dataSourceProposals.paginator = this.paginator;
    this.dataSourceProposals.sort = this.sort;
    this.lengthProposals = props.length;
  }

  setTableTeam(){
    this.dataSourceTeam = new MatTableDataSource<Student>(this.members);
    this.dataSourceTeam.paginator = this.paginator;
    this.dataSourceTeam.sort = this.sort;
    this.lengthMembers = this.members.length;
  }

  acceptProposal(token: string) {
    this.accept.emit(token);
  }

  refuseProposal(token: string) {
    this.refuse.emit(token);
  }
}
