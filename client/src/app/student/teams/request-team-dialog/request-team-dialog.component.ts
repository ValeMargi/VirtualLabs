import { Component, OnInit, Input,Output,EventEmitter } from '@angular/core';
import { ViewChild, AfterViewInit } from '@angular/core';
import {MatSort} from '@angular/material/sort';
import {MatTable} from '@angular/material/table';
import {MatPaginator} from '@angular/material/paginator';

export interface ListStudent {
  Matricola: string;
  Cognome: string;
  Nome: string;
  Stato: string;
}

const ELEMENT_DATA: ListStudent[] = [
  {Matricola: 's267782', Cognome: 'Mosconi', Nome: 'Germano', Stato: 'L'},
  {Matricola: 's268882', Cognome: 'Brazorf', Nome: 'Ajeje', Stato: 'L'},
  {Matricola: 's269982', Cognome: 'Esposito', Nome: 'Mohamed', Stato: 'L'},
  {Matricola: 's265482', Cognome: 'La Barca', Nome: 'Remo', Stato: 'L'},
  {Matricola: 's265582', Cognome: 'Saolini', Nome: 'Gianmarco', Stato: 'L'},
 
];

@Component({
  selector: 'app-request-team-dialog',
  templateUrl: './request-team-dialog.component.html',
  styleUrls: ['./request-team-dialog.component.css']
})
export class RequestTeamDialogComponent implements AfterViewInit,OnInit{
  @ViewChild('table') table: MatTable<Element>;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  displayedColumns: string[] = ['Select','Matricola', 'Cognome', 'Nome'];
  dataSource = ELEMENT_DATA;
 
  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  constructor() {}

  ngAfterViewInit(): void {
 
  }

  ngOnInit() {

  }

}

