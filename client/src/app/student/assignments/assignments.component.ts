import { Homework } from './../../models/homework.model';
import { Component, OnInit, Input,Output, AfterViewInit, ViewChild, OnChanges, SimpleChanges } from '@angular/core';
import { AssignmentsContComponent } from './assignments-cont/assignments-cont.component';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { VM } from '../../models/vm.model';
import { Assignment } from 'src/app/models/assignment.model';
import { HomeworkVersion } from 'src/app/models/homework-version.model';
import { Student } from 'src/app/models/student.model';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import {AddHomeworkContComponent } from './add-homework/add-homework-cont/add-homework-cont.component'
import { Router, ActivatedRoute } from '@angular/router';

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
  AssignmentsColumns: string[] = ['titolo', 'timestamp'];

  dataSource = new MatTableDataSource<VM>();

  dataAssignments = new MatTableDataSource<Assignment>();
  dataHomeworks = new MatTableDataSource<Homework>();
  @Input() public homeworks: Homework[] = [];
  @Input() public assignments: Assignment[] = [];

  @Output() public HOMEWORK: Homework;
  @Output() public ASSIGNMENT: Assignment;

  tableVisibility: boolean = true;
  tableAssignmetsVisibility: boolean =true;
  tableHomeworkVisibility: boolean = true;

  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  constructor(private cont: AssignmentsContComponent,private router: Router,
              private route: ActivatedRoute, private matDialog: MatDialog) { }

  ngAfterViewInit(): void {

  }

  ngOnInit(): void {
    this.tableVisibility = false;
    //this.manageAssVisibility();

  }
  ngOnChanges(changes: SimpleChanges) {
    this.assignments = changes.assignments.currentValue;
    //this.manageAssVisibility();
  }

  manageAssVisibility() {
    if (this.assignments.length > 0) {
      this.tableAssignmetsVisibility = true;
    }
    else {
      this.tableAssignmetsVisibility = false;
    }
  }

  showHomeworks(ass: Assignment) {
    //this.tableVisibility = true;
    this.ASSIGNMENT = ass;
    this.router.navigate([ass.id, 'homeworks'], { relativeTo: this.route });
  }

  showAssignments(assignment: Assignment) {
  }



  openDialogAss() {
    const dialogRef = this.matDialog.open(AddHomeworkContComponent,{width: '600px', id: 'dialogAss'});
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

}
