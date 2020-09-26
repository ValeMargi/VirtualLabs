import { Component, OnInit, AfterViewInit, Input, Output, EventEmitter, ViewChild, OnChanges, SimpleChanges } from '@angular/core';
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
import { StudentService } from 'src/app/services/student.service';
@Component({
  selector: 'app-vms-student',
  templateUrl: './vms.component.html',
  styleUrls: ['./vms.component.css']
})
export class VmsComponent implements AfterViewInit, OnInit, OnChanges {
  @ViewChild('table') table: MatTable<Element>;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  displayedColumns: string[] = ['vmName', 'status', 'link', 'swithOn','edit','swithOff'];
  dataSource = new MatTableDataSource<VM>();
  @Input() public vms: VM[];
  @Input() public team: Team;
  
  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  vmVisibility: boolean = false;
  noTeamVisibility: boolean = false;

  constructor(private studentService: StudentService, public dialog: MatDialog) { }

  ngAfterViewInit(): void {
    this.dataSource = new MatTableDataSource<VM>(this.vms);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.length = this.vms.length;
  }

  ngOnInit() {
    this.manageViews();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.team != null) {
      this.team = changes.team.currentValue;
    }

    if (changes.vms != null) {
      this.vms = changes.vms.currentValue;
    }

    this.manageViews();
  }

  manageViews() {
    if (this.team != null) {
      this.noTeamVisibility = false;

      if (this.vms.length == 0) {
        this.vmVisibility = false;
      }
      else {
        this.vmVisibility = true;
      }
    }
    else {
      this.vmVisibility = false;
      this.noTeamVisibility = true;
    }
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
