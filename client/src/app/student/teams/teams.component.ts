import { StudentService } from 'src/app/services/student.service';
import { Component, OnInit, Input, AfterViewInit, ViewChild, OnChanges, SimpleChanges, Output, EventEmitter} from '@angular/core';
import { MatTable, MatTableDataSource } from '@angular/material/table'
import{ Team } from '../../models/team.model';
import{ TeamsContComponent } from './teams-cont/teams-cont.component'
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import {MatDialog} from '@angular/material/dialog';
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

  invitati: String[] = [];

  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  constructor(private dialog: MatDialog) { }

  openRequestDialog() {
    const dialogRef = this.dialog.open(RequestTeamDialogContComponent,{ id: 'dialogRequest'});

    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
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
      this.getInvitations();

      if (this.proposals != null) {
        this.setTableProposals();
      }
    }
  }

  getInvitations() {
    if (this.myProposal == null) {
      return '';
    }

    let text: string = '';

    this.myProposal.students.forEach(s => {
      text += "[  " + s.student + " ➥ ";

      if (s.status == true) {
        text += "Richiesta accettata],";
      }
      else {
        text += "In attesa di risposta], ";
      }
    });

    let splitext = text.split(",");

    splitext.forEach(element => {
      this.invitati.push(element);
    });


    return this.invitati;
  }

  setTableProposals() {
    const props: Proposal[] = [];
    this.myPropVisibility = false;

    this.proposals.forEach(p => {
      if (p.status) {
        this.myProposal = p;
        this.myPropVisibility = true;
      }
      else {
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
