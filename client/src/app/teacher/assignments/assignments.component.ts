import { Component, OnInit, Input, AfterViewInit, ViewChild, Version, Output } from '@angular/core';
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

@Component({
  selector: 'app-assignments-teacher',
  templateUrl: './assignments.component.html',
  styleUrls: ['./assignments.component.css']
})
export class AssignmentsComponent implements AfterViewInit, OnInit {
  
  @Input() public assignments: Assignment[] = [];
  @Output() public ASSIGNMENT: Assignment;

  tableVisibility: boolean = false;

  constructor(private matDialog: MatDialog) { }

  ngAfterViewInit(): void {
  }

  ngOnInit(): void {
    this.tableVisibility = false;
  }

  showHomeworks(ass: Assignment) {
    this.tableVisibility = true;
    this.ASSIGNMENT = ass;
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

}
