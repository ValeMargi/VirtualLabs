import { Component, OnInit, AfterViewInit, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { MatTableDataSource, MatTable } from '@angular/material/table';
import { VmsContComponent } from './vms-cont.component';
import { Team } from '../../models/team.model';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ManageModelContComponent } from './manage-model-cont.component';

@Component({
  selector: 'app-vms-teacher',
  templateUrl: './vms.component.html',
  styleUrls: ['./vms.component.css']
})
export class VmsComponent implements AfterViewInit, OnInit {
  @Input() public teams: Team[];
  @Output() public TEAM: Team;
  
  teamVMsVisibility: boolean = false;

  constructor(private cont: VmsContComponent, private matDialog: MatDialog) { }

  ngAfterViewInit(): void {
    
  }

  ngOnInit() {
    
  }

  openDialogModel() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        id: 1,
        title: 'Model'
    };

    this.matDialog.open(ManageModelContComponent, dialogConfig);
  }

  selectTeam(team) {
    this.teamVMsVisibility = true;
    this.TEAM = team;
  }

}
