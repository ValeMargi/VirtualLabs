import { Component, OnInit } from '@angular/core';
import { Team } from '../../../models/team.model';


@Component({
  selector: 'app-teams-cont',
  templateUrl: '../teams-cont/teams-cont.component.html',
  styleUrls: ['../teams-cont/teams-cont.component.css']
})
export class TeamsContComponent implements OnInit {

  constructor() { }
  
  public TEAMS: Team[] = []
  public REQUEST: Request[] = []

  ngOnInit(): void {
  }

}
