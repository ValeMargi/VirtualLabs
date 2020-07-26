import { Component, OnInit, Input, AfterViewInit, ViewChild} from '@angular/core';
import{ Team } from '../../models/team.model';
import{ TeamsContComponent } from './teams-cont.component'
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { VM } from '../../models/vm.model';
import {RequestTeamDialogComponent} from './request-team-dialog/request-team-dialog.component'

import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'app-teams',
  templateUrl: '../teams/teams.component.html',
  styleUrls: ['../teams/teams.component.css']
})
export class TeamsComponent implements AfterViewInit,OnInit {

  @ViewChild('table') table: MatTable<Element>;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;


  displayedColumns: string[] = ['id', 'name', 'firstName', 'status', 'timestamp'];
  dataSource = new MatTableDataSource<VM>();
 

  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  constructor(private cont: TeamsContComponent, public dialog: MatDialog) { }
  openRequestDialog() {
    const dialogRef = this.dialog.open(RequestTeamDialogComponent,{width: '700px', id: 'dialogRequest'});
    
    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
    });
  }

  ngAfterViewInit(): void {

  }

  ngOnInit(): void {
  }

}
