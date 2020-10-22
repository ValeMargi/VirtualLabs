import { StudentService } from 'src/app/services/student.service';
import { Component, OnInit, Input, AfterViewInit, ViewChild, OnChanges, SimpleChanges, Output, EventEmitter} from '@angular/core';
import { MatTable, MatTableDataSource } from '@angular/material/table'
import{ Team } from '../../models/team.model';
import{ TeamsContComponent } from './teams-cont/teams-cont.component'
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import {RequestTeamDialogComponent} from './request-team-dialog/request-team-dialog.component'
import {MatDialog,MatDialogConfig} from '@angular/material/dialog';
import { RequestTeamDialogContComponent } from './request-team-dialog/request-team-dialog-cont/request-team-dialog-cont.component';
import { Proposal } from 'src/app/models/proposal.model';
import { Student } from 'src/app/models/student.model';

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
  displayedColumnsTeam: string[] = ['id', 'name', 'surname'];
  dataSourceTeam = new MatTableDataSource<Student>();
  tableTeamVisibility:boolean = true;

  //Table Request
  displayedColumnsRequest: string[] = ['teamName', 'creator', 'students', 'choice'];
  dataSourceProposals = new MatTableDataSource<Proposal>();
  tableRequestVisibility:boolean = true;

  @Input() public team: Team;
  @Input() public proposals: Proposal[] = [];
  @Input() public members: Student[] = [];

  @Output('accept') accept = new EventEmitter<string>();
  @Output('refuse') refuse = new EventEmitter<string>();

  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  constructor(private cont: TeamsContComponent,
              public dialog: MatDialog,
              private studentService: StudentService) { }

  openRequestDialog() {
    const dialogRef = this.dialog.open(RequestTeamDialogContComponent,{ id: 'dialogRequest'});

    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
    });
  }

  ngAfterViewInit(): void {
    /*this.dataSourceTeam.paginator = this.paginator;
    this.dataSourceTeam.sort = this.sort;
    this.length = this.teams.length;*/
  }

  ngOnInit(): void {
    this.dataSourceTeam.sort = this.sort;
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.team != null) {
      this.team = changes.team.currentValue;
      this.setTableTeam();
    }

    if (changes.proposals != null) {
      this.proposals = changes.proposals.currentValue;
      this.setTableProposals();
    }
  }

  setTableProposals() {
    this.dataSourceProposals = new MatTableDataSource<Proposal>(this.proposals);
    this.dataSourceProposals.paginator = this.paginator;
    this.dataSourceProposals.sort = this.sort;
    this.length = this.proposals.length;
  }


  setTableTeam(){
    this.dataSourceTeam = new MatTableDataSource<Student>(this.members);
    this.dataSourceTeam.paginator = this.paginator;
    this.dataSourceTeam.sort = this.sort;
    this.length = this.members.length;
  }

  acceptProposal(token: string) {
    this.accept.emit(token);
  }

  refuseProposal(token: string) {
    this.refuse.emit(token);
  }
}
