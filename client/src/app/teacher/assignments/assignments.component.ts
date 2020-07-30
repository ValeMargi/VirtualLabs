import { Component, OnInit, Input, AfterViewInit, ViewChild, Version } from '@angular/core';
import { AssignmentsContComponent } from './assignments-cont.component';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { VM } from '../../models/vm.model';
import { Homework } from 'src/app/models/homework.model';
import { Assignment } from 'src/app/models/assignment.model';
import { HomeworkVersion } from 'src/app/models/homework-version.model';
import { Student } from 'src/app/models/student.model';
import { CreateAssignmentContComponent } from './create-assignment-cont.component';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';

@Component({
  selector: 'app-assignments-teacher',
  templateUrl: './assignments.component.html',
  styleUrls: ['./assignments.component.css']
})
export class AssignmentsComponent implements AfterViewInit, OnInit {
  @ViewChild('table') table: MatTable<Element>;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  displayedColumns: string[] = ['id', 'name', 'firstName', 'status', 'timestamp'];
  dataSource = new MatTableDataSource<Homework>();
  @Input() public homeworks: Homework[] = [];
  @Input() public assignments: Assignment[] = [];

  versions: HomeworkVersion[] = [];
  reviews: HomeworkVersion[] = [];

  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  tableVisibility: boolean = false;
  versionsVisibility: boolean = false;

  constructor(private cont: AssignmentsContComponent, private matDialog: MatDialog) { }

  ngAfterViewInit(): void {

    //provvisorio
    this.assignments.push(new Assignment("LAB01", "Laboratorio 1", "T01", "C01", "01/01/2020", "31/07/2020"));
    this.assignments.push(new Assignment("LAB02", "Laboratorio 2", "T01", "C01", "01/01/2020", "31/07/2020"));
  
    this.homeworks.push(new Homework("primo", "new", false, ""));
    this.dataSource = new MatTableDataSource<Homework>(this.homeworks);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.length = this.assignments.length;

    this.versions.push(new HomeworkVersion(new Student("s268746", "Carlo", "Verdone", ""), 1, "timestamp1"));
    this.versions.push(new HomeworkVersion(new Student("s268746", "Carlo", "Verdone", ""), 2, "timestamp2"));
    this.versions.push(new HomeworkVersion(new Student("s268746", "Carlo", "Verdone", ""), 2, ""));
    this.reviews.push(new HomeworkVersion(new Student("s268746", "Carlo", "Verdone", ""), 2, ""));
    this.reviews.push(new HomeworkVersion(new Student("s268746", "Carlo", "Verdone", ""), 2, ""));
    this.reviews.push(new HomeworkVersion(new Student("s268746", "Carlo", "Verdone", ""), 3, "timestamp3"))

  }

  ngOnInit(): void {
    this.tableVisibility = false;
    this.versionsVisibility = false;
  }

  showHomeworks(ass: Assignment) {
    this.tableVisibility = true;
    this.versionsVisibility = false;
  }

  openDialogAss() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        id: 1,
        title: 'Assignment'
    };

    this.matDialog.open(CreateAssignmentContComponent, dialogConfig);
  }

  showHistory(homework: Homework) {
    this.tableVisibility = false;
    this.versionsVisibility = true;
  }

  back() {
    this.tableVisibility = true;
    this.versionsVisibility = false;
  }

}
