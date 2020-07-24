import { Component, OnInit, AfterViewInit, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { MatTableDataSource, MatTable } from '@angular/material/table';
import { VM } from '../../models/vm.model';
import { VmsContComponent } from './vms-cont.component';
import { SelectionModel } from '@angular/cdk/collections';
import { FormControl } from '@angular/forms';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatCheckbox } from '@angular/material/checkbox';
import { Team } from '../../models/team.model';

@Component({
  selector: 'app-vms-student',
  templateUrl: './vms.component.html',
  styleUrls: ['./vms.component.css']
})
export class VmsComponent implements AfterViewInit, OnInit {
  @ViewChild('table') table: MatTable<Element>;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  displayedColumns: string[] = ['vmName', 'owner', 'status', 'link', 'edit'];
  dataSource = new MatTableDataSource<VM>();
  @Input() public vms: VM[];
  @Input() public teams: Team[];
  
  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  constructor(private cont: VmsContComponent) { }

  ngAfterViewInit(): void {
    /*this.cont.vms.subscribe(vv => {
      this.vms = vv;
      this.dataSource = new MatTableDataSource<VM>(this.vms);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      this.length = this.vms.length;
    });*/

    //provvisorio
    this.teams.push(new Team("T01", "Gruppo 1", 1));
    this.teams.push(new Team("T02", "Gruppo 2", 1));

    this.vms.push(new VM("VM01", 2, 100, 2, "spenta"));
    this.dataSource = new MatTableDataSource<VM>(this.vms);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.length = this.vms.length;
  }

  ngOnInit() {
    this.dataSource.sort = this.sort;
  }

  openDialogEdit() {

  }

  getStatus(on: boolean) {
    if (on) {
      return "Accesa";
    }
    else {
      return "Spenta";
    }
  }

}
