import { Component, OnInit, Input, AfterViewInit, ViewChild, Version, Output, OnChanges, SimpleChanges } from '@angular/core';
import { AssignmentsContComponent } from './assignments-cont.component';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { VM } from '../../models/vm.model';
import { Homework } from 'src/app/models/homework.model';
import { Assignment } from 'src/app/models/assignment.model';
import { HomeworkVersion } from 'src/app/models/homework-version.model';
import { Student } from 'src/app/models/student.model';
import { CreateAssignmentContComponent } from '../assignments/create-assignment/create-assignment-cont.component';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { VersionsComponent } from './versions.component';
import { HomeworksComponent } from './homeworks.component';
import { Router, ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { ViewImageContComponent } from 'src/app/view-image/view-image-cont/view-image-cont.component';

@Component({
  selector: 'app-assignments-teacher',
  templateUrl: './assignments.component.html',
  styleUrls: ['./assignments.component.css']
})
export class AssignmentsComponent implements AfterViewInit, OnInit, OnChanges {

  @ViewChild('table') table: MatTable<Element>;
  
  private sort: MatSort;
  private paginator: MatPaginator;
  
  @ViewChild(MatSort) set matSort(ms: MatSort) {
    this.sort = ms;
    this.setDataSourceAttributes();
  }

  @ViewChild(MatPaginator) set matPaginator(mp: MatPaginator) {
    this.paginator = mp;
    this.setDataSourceAttributes();
  }


  @Input() public assignments: Assignment[] = [];

  HomeworkColumns: string[] = ['id', 'name', 'firstName', 'status', 'timestamp'];
  AssignmentsColumns: string[] = ['assignmentName', 'releaseDate','expiration','showAssignment', 'showHomeworks'];

  dataAssignments = new MatTableDataSource<Assignment>();
  dataHomeworks = new MatTableDataSource<Homework>();

  assTableVisibility: boolean = true;
  tableAssignmentsVisibility: boolean =true;
  tableHomeworkVisibility: boolean = false;
  buttonHomeworkVisibility:boolean = false;

  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  constructor(private matDialog: MatDialog,
              private router: Router,
              private route: ActivatedRoute, 
              private location: Location) { }

  ngAfterViewInit(): void {
  }

  ngOnInit(): void {
    this.manageAssVisibility();
    this.setTable();
  }

  setTable() {
    this.dataAssignments = new MatTableDataSource<Assignment>(this.assignments);
    this.setDataSourceAttributes();
    this.length = this.assignments.length;
  }

  setDataSourceAttributes() {
    this.dataAssignments.paginator = this.paginator;
    this.dataAssignments.sort = this.sort;
  }

  backAssignmetsTable() {
    this.tableAssignmentsVisibility = true;
    this.location.back();
  }

  ngOnChanges(changes: SimpleChanges) {
    this.assignments = changes.assignments.currentValue;
    this.manageAssVisibility();
    this.setTable();
  }

  manageAssVisibility() {
    if (this.assignments.length > 0) {
      this.tableAssignmentsVisibility = true;
    }
    else {
      this.tableAssignmentsVisibility = false;
    }
  }

  onRouterOutletActivate(event: any) {
    this.assTableVisibility = false;
    this.tableHomeworkVisibility = true;
  }

  onRouterOutletDeactivate(event: any) {
    this.assTableVisibility = true;
    this.tableHomeworkVisibility = false;
  }

  showHomeworks(ass: Assignment) {
    this.router.navigate([ass.id, 'homeworks'], { relativeTo: this.route });
  }

  openDialogAss() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        title: 'AssignmentCreate'
    };

    this.matDialog.open(CreateAssignmentContComponent, dialogConfig);
  }


  openDialogImage(ass: Assignment) {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        title: 'AssignmentText',
        isTeacher: true,
        type: "assignment",
        assignmentId: ass.id
    };

    this.matDialog.open(ViewImageContComponent, dialogConfig);
  }

}
