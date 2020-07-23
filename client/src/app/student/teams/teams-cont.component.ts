import { Component, OnInit } from '@angular/core';
import { Team } from '../../models/team.model';

@Component({
  selector: 'app-teams-cont',
  templateUrl: '../teams/teams-cont.component.html',
  styleUrls: ['../teams/teams-cont.component.css']
})
export class TeamsContComponent implements OnInit {

  constructor() { }
  
  public TEAMS: Team[] = []

  ngOnInit(): void {
  }

}
