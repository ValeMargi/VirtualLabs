import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { VM } from '../vm.model';
import { AuthService } from '../auth/auth.service';
import { VmService } from '../services/vm.service';
import { TeamService } from '../services/team.service';
import { Team } from '../team.model';

@Component({
  selector: 'app-vms-cont',
  templateUrl: './vms-cont.component.html',
  styleUrls: ['./vms-cont.component.css']
})
export class VmsContComponent implements OnInit {

  public VMs: VM[] = []
  public COURSE_TEAMS: Team[] = []

  constructor(public vmService: VmService, public teamService: TeamService, public authService: AuthService) { 
    
  }

  @Output() vms = new EventEmitter<VM[]>()
  @Output() teams = new EventEmitter<Team>()

  ngOnInit(): void {

    
    /*this.vmService.query().subscribe(
      (data) => {
        this.VMs = data;
        this.vms.emit(this.VMs);
      },
      (error) => {  } 
      );

      this.vmService.vms("1").subscribe(
        (data) => {
          console.log(data);
          this.VMs = data;
          this.vms.emit(this.VMs);
        },
        (error) => {  } 
        );*/
  }

}
