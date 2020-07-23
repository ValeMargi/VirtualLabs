import { Component, OnInit, Input, AfterViewInit, ViewChild } from '@angular/core';
import { Assignment } from '../../models/assignment.model';
import { Delivery } from '../../models/delivery.model';
import { AssignmentsContComponent } from './assignments-cont.component';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { VM } from '../../models/vm.model';

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
  dataSource = new MatTableDataSource<VM>();
  @Input() public deliveries: Delivery[] = [];
  @Input() public assignments: Assignment[] = [];

  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  constructor(private cont: AssignmentsContComponent) { }

  ngAfterViewInit(): void {

    //provvisorio
    this.deliveries.push(new Delivery("D01", "Laboratorio 1", "T01", "C01", "01/01/2020", "31/07/2020"));
  }

  ngOnInit(): void {

  }

  showAssignments(delivery: Delivery) {
    
  }

}
