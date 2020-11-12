import { Component, OnInit, AfterViewInit, Input, Output, EventEmitter, ViewChild, OnChanges, SimpleChanges, OnDestroy } from '@angular/core';
import { MatTableDataSource, MatTable } from '@angular/material/table';
import { VmsContComponent } from './vms-cont.component';
import { Team } from '../../models/team.model';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ManageModelContComponent } from './manage-model-cont.component';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-vms-teacher',
  templateUrl: './vms.component.html',
  styleUrls: ['./vms.component.css']
})
export class VmsComponent implements OnInit, OnChanges, OnDestroy {
  @Input() teams: Team[];

  teamVMsVisibility: boolean = false;
  teamsVisibility: boolean = false;

  routeQueryParams$: Subscription;

  constructor(private matDialog: MatDialog,
              private router: Router,
              private route: ActivatedRoute) { }

  ngOnInit() {
    this.manageTeamsVisibility();

    this.routeQueryParams$ = this.route.queryParams.subscribe(params => {
      if (params['editModel']) {
        this.openDialogModel();
      }
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    this.teams = changes.teams.currentValue;
    this.manageTeamsVisibility();
  }

  ngOnDestroy() {
    this.routeQueryParams$.unsubscribe();
  }

  onRouterOutletActivate(event: any) {
    this.teamVMsVisibility = true;
  }

  onRouterOutletDeactivate(event: any) {
    this.teamVMsVisibility = false;
  }

  manageTeamsVisibility() {
    if (this.teams.length > 0) {
      this.teamsVisibility = true;
    }
    else {
      this.teamsVisibility = false;
    }
  }

  routeToEditModel() {
    this.router.navigate([], {queryParams: {editModel : "true"}});
  }

  openDialogModel() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    const dialogRef = this.matDialog.open(ManageModelContComponent, dialogConfig);

    dialogRef.componentInstance.courseName = this.route.snapshot.params.courses;

    dialogRef.afterClosed().subscribe(result => {
      const queryParams = {}
      this.router.navigate([], { queryParams, replaceUrl: true, relativeTo: this.route });
    });
  }

  selectTeam(team) {
    this.router.navigate(['team', team.id], { relativeTo: this.route });
  }

}
