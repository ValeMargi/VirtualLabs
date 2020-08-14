import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { VM } from '../../models/vm.model';
import { AuthService } from '../../auth/auth.service';
import { TeamService } from '../../services/team.service';
import { Team } from '../../models/team.model';

@Component({
  selector: 'app-vms-cont',
  templateUrl: './vms-cont.component.html',
  styleUrls: ['./vms-cont.component.css']
})
export class VmsContComponent implements OnInit {
  
  public COURSE_TEAMS: Team[] = []

  constructor(public teamService: TeamService, public authService: AuthService) { 
    
  }

  ngOnInit(): void {
    //provvisorio
    this.COURSE_TEAMS.push(new Team(-1, "Gruppo 1", 1, 0, 0, 0, 0, 0));
    this.COURSE_TEAMS.push(new Team(-1, "Gruppo 2", 1, 0, 0, 0, 0, 0));
  }

}
