import { Component, OnInit, Input, AfterViewInit, ViewChild} from '@angular/core';
import { MatTable, MatTableDataSource } from '@angular/material/table'
import{ Team } from '../../models/team.model';
import{ TeamsContComponent } from './teams-cont/teams-cont.component'
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { VM } from '../../models/vm.model';
import {RequestTeamDialogComponent} from './request-team-dialog/request-team-dialog.component'
import {MatDialog,MatDialogConfig} from '@angular/material/dialog';

@Component({
  selector: 'app-teams',
  templateUrl: '../teams/teams.component.html',
  styleUrls: ['../teams/teams.component.css']
})
export class TeamsComponent implements AfterViewInit,OnInit {

  @ViewChild('table') table: MatTable<Element>;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;


  displayedColumnsTeam: string[] = ['id', 'name', 'status'];
  dataSourceTeam = new MatTableDataSource<Team>();

    //Se uno studente è iscritto ad un gruppo
    tableTeamVisibility:boolean = true;

  displayedColumnsRequest: string[] = ['id', 'name', 'firstName','groupName', 'status', 'timestamp','scelta'];
  dataSourceRequest = new MatTableDataSource<Team>();

  @Input() public teams: Team[] = [];
  @Input() public request: Request[] = [];

  //Input per la tabella richieste
  tableVisibility: boolean = true;

  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  constructor(private cont: TeamsContComponent, public dialog: MatDialog) { }

  openRequestDialog() {
    const dialogRef = this.dialog.open(RequestTeamDialogComponent,{width: '600px', id: 'dialogRequest'});

    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
    });
  }

  ngAfterViewInit(): void {
    //Inserimento provvisorio
    this.teams.push(new Team(-1, "Vassallo Giorgio", 2, 0, 0, 0, 0, 0));
    this.teams.push(new Team(-1, "Cordellic Marco", 1, 0, 0, 0, 0, 0));

    this.dataSourceTeam.paginator = this.paginator;
    this.dataSourceTeam.sort = this.sort;
    this.length = this.teams.length;
  }

  ngOnInit(): void {
    this.dataSourceTeam.sort = this.sort;
  }

}
