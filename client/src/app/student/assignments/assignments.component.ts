import { Component, OnInit, Input,Output, AfterViewInit, ViewChild, OnChanges, SimpleChanges, OnDestroy, EventEmitter } from '@angular/core';
import { AssignmentsContComponent } from './assignments-cont/assignments-cont.component';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { Assignment } from 'src/app/models/assignment.model';
import { Homework } from './../../models/homework.model';
import { HomeworkVersion } from 'src/app/models/homework-version.model';
import { Student } from 'src/app/models/student.model';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import {AddHomeworkContComponent } from './add-homework/add-homework-cont/add-homework-cont.component'
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { ViewImageContComponent } from 'src/app/view-image/view-image-cont/view-image-cont.component';
import { Subscription } from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-assignments-student',
  templateUrl: './assignments.component.html',
  styleUrls: ['./assignments.component.css']
})
export class AssignmentsComponent implements AfterViewInit, OnInit, OnChanges, OnDestroy {
  @ViewChild('table') table: MatTable<Element>;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  HomeworkColumns: string[] = ['id', 'name', 'firstName', 'status', 'timestamp'];
  AssignmentsColumns: string[] = ['assignmentName', 'releaseDate','expiration','showAssignment', 'showVersions'];

  dataAssignments = new MatTableDataSource<Assignment>();

  @Input() hasTeam: boolean;
  @Input() hasVM: boolean;
  @Input() assignments: Assignment[] = [];
  @Output() ASSIGNMENT: Assignment;

  versionsVisibility: boolean = false;
  tableAssignmentsVisibility: boolean = false;
  buttonHomeworkVisibility:boolean = false;

  panelOpenState = false;
  titolo: string;
  public assId: number;

  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  constructor(private router: Router,
              private route: ActivatedRoute,
              private matDialog: MatDialog) { }

  ngAfterViewInit(): void {
  }

  ngOnInit(): void {
    this.setTable();
    this.manageAssVisibility();
  }

  ngOnDestroy() {
    
  }

  setTable() {
    this.dataAssignments = new MatTableDataSource<Assignment>(this.assignments);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.assignments != null) {
      this.assignments = changes.assignments.currentValue;
    }

    this.manageAssVisibility();
    this.setTable();
  }

  onRouterOutletActivate(event: any) {
    this.versionsVisibility = true;
  }

  onRouterOutletDeactivate(event: any) {
    this.versionsVisibility = false;
  }

  showVersions(ass: Assignment) {
    if (!this.hasTeam) {
      window.alert("Bisogna essere in un team per accedere a questa sezione");
      return;
    }

    if (!this.hasVM) {
      window.alert("Il team deve avere almeno una VM per accedere a questa sessione");
      return;
    }

    this.router.navigate([ass.id, 'versions'], { relativeTo: this.route });
  }

  manageAssVisibility() {
    if (this.assignments.length > 0) {
      this.tableAssignmentsVisibility = true;
    }
    else {
      this.tableAssignmentsVisibility = false;
    }
  }

  openDialogImage(ass: Assignment) {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        title: 'AssignmentText',
        isTeacher: false,
        type: "assignment",
        assignmentId: ass.id
    };

    this.matDialog.open(ViewImageContComponent, dialogConfig);
  }

}
