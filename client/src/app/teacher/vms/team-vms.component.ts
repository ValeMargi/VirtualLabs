import { Component, OnInit, Input, ViewChild, OnChanges, SimpleChanges } from '@angular/core';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { VM } from 'src/app/models/vm.model';
import { VMOwners } from 'src/app/models/vm-owners.model';
import { Student } from 'src/app/models/student.model';

@Component({
  selector: 'app-team-vms',
  templateUrl: './team-vms.component.html',
  styleUrls: ['./team-vms.component.css']
})
export class TeamVmsComponent implements OnInit, OnChanges {
  @ViewChild('table') table: MatTable<Element>;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  displayedColumns: string[] = ['vmName', 'owners', 'status', 'link'];
  dataSource = new MatTableDataSource<VMOwners>();
  
  @Input() public vms: VMOwners[];

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
      this.dataSource = new MatTableDataSource<VMOwners>(this.vms);
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

  getOwners(owners: Student[]) {
    let str: string = "";

    owners.forEach(s => {
      str = str.concat(s.name + " " + s.firstName);

      if (owners.indexOf(s) < owners.length - 1)
        str = str.concat(", ");
    });

    return str;
  }

  openVM() {

  }

}
