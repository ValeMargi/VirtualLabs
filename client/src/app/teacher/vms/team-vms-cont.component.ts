import { Component, OnInit, Output, Input } from '@angular/core';
import { Team } from 'src/app/models/team.model';
import { VM } from 'src/app/models/vm.model';

@Component({
  selector: 'app-team-vms-cont',
  templateUrl: './team-vms-cont.component.html',
  styleUrls: ['./team-vms-cont.component.css']
})
export class TeamVmsContComponent implements OnInit {

  @Input() public team: Team;
  @Output() public VMs: VM[] = []

  constructor() { }

  ngOnInit(): void {
    this.VMs.push(new VM("VM01", 2, 100, 2, "spenta"));
  }

}
