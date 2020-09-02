import { Component, OnInit, Input, ViewChild, OnChanges, SimpleChanges } from '@angular/core';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { VM } from 'src/app/models/vm.model';

@Component({
  selector: 'app-team-vms',
  templateUrl: './team-vms.component.html',
  styleUrls: ['./team-vms.component.css']
})
export class TeamVmsComponent implements OnInit, OnChanges {
  @ViewChild('table') table: MatTable<Element>;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  displayedColumns: string[] = ['vmName', 'owner', 'status', 'link'];
  dataSource = new MatTableDataSource<VM>();
  
  @Input() public vms: VM[];

  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10];

  VMsVisibility: boolean = false;

  constructor() { }

  ngOnInit(): void {
    this.manageTable();
  }

  ngOnChanges(changes: SimpleChanges) {
    this.vms = changes.vms.currentValue;
    this.manageTable();
  }

  manageTable() {
    if (this.vms.length > 0) {
      this.VMsVisibility = true;
      this.dataSource = new MatTableDataSource<VM>(this.vms);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      this.length = this.vms.length;
    }
    else {
      this.VMsVisibility = false;
    }
  }

  getStatus(status: string) {
    if (status.match("on")) {
      return "Accesa";
    }
    else {
      return "Spenta";
    }
  }

  openVM() {

  }

}
