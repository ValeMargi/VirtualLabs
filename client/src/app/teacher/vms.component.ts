import { Component, OnInit, AfterViewInit, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { MatTableDataSource, MatTable } from '@angular/material/table';
import { VM } from '../vm.model';
import { VmsContComponent } from './vms-cont.component';
import { SelectionModel } from '@angular/cdk/collections';
import { FormControl } from '@angular/forms';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatCheckbox } from '@angular/material/checkbox';
import { Team } from '../team.model';

@Component({
  selector: 'app-vms',
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
    this.teams.push(new Team("T01", "Gruppo 1", 1, 1, null));
    this.teams.push(new Team("T02", "Gruppo 2", 1, 1, null));

    this.vms.push(new VM("VM01", "Ubuntu", "T01", false, "s267782", null));
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

}
