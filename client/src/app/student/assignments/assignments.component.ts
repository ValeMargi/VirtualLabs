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
import { Router, ActivatedRoute } from '@angular/router';
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
  AssignmentsColumns: string[] = ['assignmentName', 'releaseDate','expiration','showAssignment'];

  dataAssignments = new MatTableDataSource<Assignment>();

  @Input() homework: Homework;
  @Input() assignments: Assignment[] = [];
  @Output() HOMEWORK: Homework;
  @Output() ASSIGNMENT: Assignment;
  @Output('versions') versions = new EventEmitter<Assignment>()

  tableVisibility: boolean = false;
  versionsVisibility: boolean = false;

  buttonHomeworkVisibility:boolean = false;

  panelOpenState = false;
  titolo: string;
  public assId: number;
  private route$: Subscription

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

    this.route$ = this.route.params.subscribe(params => {
      let id = params.id;
      console.log(id)

      if (id == undefined) {
        this.versionsVisibility = false;
      }
    });
  }

  ngOnDestroy() {
    this.route$.unsubscribe();
  }

  setTable() {
    this.dataAssignments = new MatTableDataSource<Assignment>(this.assignments);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.assignments != null) {
      this.assignments = changes.assignments.currentValue;
    }

    if (changes.homework != null && this.ASSIGNMENT != null) {
      this.homework = changes.homework.currentValue;
      this.HOMEWORK = this.homework;
      this.router.navigate([this.ASSIGNMENT.id, 'versions'], { relativeTo: this.route })
    }

    this.manageAssVisibility();
    this.setTable();
  }

  showVersions(ass: Assignment) {
    this.versions.emit(ass);
    this.versionsVisibility = true;
    this.ASSIGNMENT = ass;
    //this.router.navigate([ass.id, 'versions'], { relativeTo: this.route });
  }

  manageAssVisibility() {
    /*if (this.assignments.length > 0) {
      this.tableAssignmetsVisibility = true;
    }
    else {
      this.tableAssignmetsVisibility = false;
    }*/
  }

  openDialogHomework(assId:number) {
    const dialogRef = this.matDialog.open(AddHomeworkContComponent,{ id: 'dialogHomework'});
    const dialogConfig = new MatDialogConfig();

    dialogRef.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogRef.componentInstance.assId = assId;

    dialogConfig.data = {
        id: 1,
        title: 'Assignment'
    };

    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
    });

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
