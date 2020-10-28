import { Component, OnInit, AfterViewInit, Input, Output, EventEmitter, ViewChild, OnChanges, SimpleChanges } from '@angular/core';
import { MatTableDataSource, MatTable } from '@angular/material/table';
import { VmsContComponent } from './vms-cont.component';
import { Team } from '../../models/team.model';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ManageModelContComponent } from './manage-model-cont.component';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-vms-teacher',
  templateUrl: './vms.component.html',
  styleUrls: ['./vms.component.css']
})
export class VmsComponent implements AfterViewInit, OnInit, OnChanges {
  @Input() public teams: Team[];
  @Output() public TEAM: Team;
  
  teamVMsVisibility: boolean = false;
  teamsVisibility: boolean = false;

  constructor(private matDialog: MatDialog, private router: Router, private route: ActivatedRoute) { }

  ngAfterViewInit(): void {
    
  }

  ngOnInit() {
    this.manageTeamsVisibility();
  }

  ngOnChanges(changes: SimpleChanges) {
    this.teams = changes.teams.currentValue;
    this.manageTeamsVisibility();
  }

  manageTeamsVisibility() {
    if (this.teams.length > 0) {
      this.teamsVisibility = true;
    }
    else {
      this.teamsVisibility = false;
    }
  }

  openDialogModel() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = false;

    const dialogRef = this.matDialog.open(ManageModelContComponent, dialogConfig);

    dialogRef.componentInstance.courseName = this.route.snapshot.params.courses;
  }

  selectTeam(team) {
    //this.teamVMsVisibility = true;
    this.TEAM = team;
    this.router.navigate(['team', team.id], { relativeTo: this.route });
  }

}
