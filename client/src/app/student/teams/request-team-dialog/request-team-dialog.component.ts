import { Component, OnInit } from '@angular/core';
import {MatDialog} from '@angular/material/dialog';


/**
 * @title Request-Dialog with header, scrollable content and actions
 */


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
export class RequestTeamDialogComponent {

  constructor() { }

  displayedColumns: string[] = ['Matricola', 'Cognome', 'Nome', 'Stato'];
  dataSource = ELEMENT_DATA;

}

