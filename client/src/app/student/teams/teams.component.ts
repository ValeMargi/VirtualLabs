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

  //Table Request Accepted
  displayedColumnsRequestAccepted: string[] = ['teamName', 'creator', 'students'];
  dataSourceProposalsAccepted = new MatTableDataSource<Proposal>();

  //My proposal
  myProposal: Proposal;

  @Input() public team: Team;
  @Input() public proposals: Proposal[] = [];
  @Input() public members: Student[] = [];
  @Output('accept') accept = new EventEmitter<string>();
  @Output('refuse') refuse = new EventEmitter<string>();

  lengthProposals: number = 0;
  lengthProposalsAccepted: number = 0;
  lengthMembers: number = 0;
  teamName: string;

  propsVisibility: boolean = false;
  propsAcceptedVisibility: boolean = false;
  myPropVisibility: boolean = false;

  invited: string[] = [];

  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  constructor(private dialog: MatDialog,
              private studentService: StudentService) { }

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
    const propsAccepted: Proposal[] = [];
    this.myProposal = null;
    
    if (this.proposals.length == 0) {
      this.propsVisibility = false;
      this.propsAcceptedVisibility = false;
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

        if (p.status) {
          propsAccepted.push(p);
        }
        else {
          props.push(p);
        }
      }
    });

    if (this.myProposal != null) {
      this.myPropVisibility = true;
    }
    else {
      this.myPropVisibility = false;
    }

    if (props.length > 0) {
      this.propsVisibility = true;
    }
    else {
      this.propsVisibility = false;
    }

    if (propsAccepted.length > 0) {
      this.propsAcceptedVisibility = true;
    }
    else {
      this.propsAcceptedVisibility = false;
    }

    if (this.propsVisibility) { 
      this.dataSourceProposals = new MatTableDataSource<Proposal>(props);
      this.dataSourceProposals.paginator = this.paginator;
      this.dataSourceProposals.sort = this.sort;
      this.lengthProposals = props.length;
    }

    if (this.propsAcceptedVisibility) { 
      this.dataSourceProposalsAccepted = new MatTableDataSource<Proposal>(propsAccepted);
      this.dataSourceProposalsAccepted.paginator = this.paginator;
      this.dataSourceProposalsAccepted.sort = this.sort;
      this.lengthProposalsAccepted = propsAccepted.length;
    }
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
