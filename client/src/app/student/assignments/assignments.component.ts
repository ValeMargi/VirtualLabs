import { Homework } from './../../models/homework.model';
import { Component, OnInit, Input,Output, AfterViewInit, ViewChild, OnChanges, SimpleChanges } from '@angular/core';
import { AssignmentsContComponent } from './assignments-cont/assignments-cont.component';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { Assignment } from 'src/app/models/assignment.model';
import { HomeworkVersion } from 'src/app/models/homework-version.model';
import { Student } from 'src/app/models/student.model';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import {AddHomeworkContComponent } from './add-homework/add-homework-cont/add-homework-cont.component'
import { Router, ActivatedRoute } from '@angular/router';
import { ViewImageContComponent } from 'src/app/view-image/view-image-cont/view-image-cont.component';

@Component({
  selector: 'app-assignments-student',
  templateUrl: './assignments.component.html',
  styleUrls: ['./assignments.component.css']
})
export class AssignmentsComponent implements AfterViewInit, OnInit {
  @ViewChild('table') table: MatTable<Element>;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  HomeworkColumns: string[] = ['id', 'name', 'firstName', 'status', 'timestamp'];
  AssignmentsColumns: string[] = ['assignmentName', 'releaseDate','expiration','showAssignment'];

  dataAssignments = new MatTableDataSource<Assignment>();
  dataHomeworks = new MatTableDataSource<Homework>();

  @Input() public homeworks: Homework[] = [];
  @Input() public assignments: Assignment[] = [];
  @Output() public HOMEWORK: Homework;
  @Output() public ASSIGNMENTS: Assignment;

  tableVisibility: boolean = true;
  tableAssignmetsVisibility: boolean =true;
  tableHomeworkVisibility: boolean = false;

  buttonHomeworkVisibility:boolean = false;

  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  constructor(private cont: AssignmentsContComponent,
              private router: Router,
              private route: ActivatedRoute,
              private matDialog: MatDialog) { }

  ngAfterViewInit(): void {
  }

  ngOnInit(): void {
    this.tableVisibility = false;
    this.setTable();
    //this.manageAssVisibility();
  }

  setTable() {
    this.dataAssignments = new MatTableDataSource<Assignment>(this.assignments);

  }

  ngOnChanges(changes: SimpleChanges) {
    this.assignments = changes.assignments.currentValue;
    this.setTable();

  }

  titolo: string;

  openHomeworkTable(ass:Assignment){
      this.tableHomeworkVisibility = true;

       this.titolo = ass.assignmentName;
  }
  manageAssVisibility() {
    if (this.assignments.length > 0) {
      this.tableAssignmetsVisibility = true;
    }
    else {
      this.tableAssignmetsVisibility = false;
    }
  }

  openDialogHomework() {
    const dialogRef = this.matDialog.open(AddHomeworkContComponent,{ id: 'dialogHomework'});
    const dialogConfig = new MatDialogConfig();

    dialogRef.disableClose = false;
    dialogConfig.autoFocus = true;

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
