import { Component, OnInit, AfterViewInit, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { MatTableDataSource, MatTable } from '@angular/material/table';
import { VM } from '../../models/vm.model';
import { VmsContComponent } from './vms-cont/vms-cont.component';
import { SelectionModel } from '@angular/cdk/collections';
import { FormControl } from '@angular/forms';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatCheckbox } from '@angular/material/checkbox';
import { Team } from '../../models/team.model';
import { CreateVmsContComponent } from './create-vms/create-vms-cont/create-vms-cont.component';
import {MatDialog,MatDialogConfig} from '@angular/material/dialog';
@Component({
  selector: 'app-vms-student',
  templateUrl: './vms.component.html',
  styleUrls: ['./vms.component.css']
})
export class VmsComponent implements AfterViewInit, OnInit {
  @ViewChild('table') table: MatTable<Element>;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  displayedColumns: string[] = ['vmName', 'status', 'link', 'swithOn','edit','swithOff'];
  dataSource = new MatTableDataSource<VM>();
  @Input() public vms: VM[];
  @Input() public teams: Team[];
  
  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  constructor(private cont: VmsContComponent,public dialog: MatDialog) { }

  ngAfterViewInit(): void {
    /*this.cont.vms.subscribe(vv => {
      this.vms = vv;
      this.dataSource = new MatTableDataSource<VM>(this.vms);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      this.length = this.vms.length;
    });*/

    this.vms.push(new VM(-1, 2, 100, 2, "off", "VM01", ""));
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

  swithOnVm(){}
  switchOffVm(){}

  getStatus(on: boolean) {
    if (on) {
      return "Accesa";
    }
    else {
      return "Spenta";
    }
  }

  openCreateVmsDialog() {
    const dialogRef = this.dialog.open(CreateVmsContComponent,{ id: 'dialogCreteVms'});
    
    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
    });
  }

}
