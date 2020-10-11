import { Component, OnInit, AfterViewInit, Input, Output, EventEmitter, ViewChild, OnChanges, SimpleChanges } from '@angular/core';
import { MatTableDataSource, MatTable } from '@angular/material/table';
import { VM } from '../../models/vm.model';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { Team } from '../../models/team.model';
import { CreateVmsContComponent } from './create-vms/create-vms-cont/create-vms-cont.component';
import {MatDialog,MatDialogConfig} from '@angular/material/dialog';
import { StudentService } from 'src/app/services/student.service';
import { VMOwners } from 'src/app/models/vm-owners.model';
import { Student } from 'src/app/models/student.model';
import { ManageVmContComponent } from './manage-vm/manage-vm-cont/manage-vm-cont.component';
@Component({
  selector: 'app-vms-student',
  templateUrl: './vms.component.html',
  styleUrls: ['./vms.component.css']
})
export class VmsComponent implements AfterViewInit, OnInit, OnChanges {
  @ViewChild('table') table: MatTable<Element>;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  displayedColumns: string[] = ['vmName', 'owners', 'status', 'link', 'swithOnOff', 'edit'];
  dataSource = new MatTableDataSource<VMOwners>();
  @Input() public vms: VMOwners[];
  @Input() public team: Team;

  @Output('activate') activate = new EventEmitter<number>();
  @Output('disable') disable = new EventEmitter<number>();

  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  vmVisibility: boolean = true;
  noTeamVisibility: boolean = true;

  constructor(private studentService: StudentService, public dialog: MatDialog) { }

  ngAfterViewInit(): void {

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

    //this.manageViews();
  }

  manageViews() {
    if (this.team != null) {
      this.noTeamVisibility = false;

      if (this.vms.length == 0) {
        this.vmVisibility = false;
      }
      else {
        this.vmVisibility = true;
        this.manageTable();
      }
    }
    else {
      this.vmVisibility = false;
      this.noTeamVisibility = true;
    }
  }

  manageTable() {
    this.dataSource = new MatTableDataSource<VMOwners>(this.vms);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.length = this.vms.length;
  }

  openDialogEdit(vm: VMOwners) {
    let isOwner: boolean = false;

    vm.owners.forEach(s => {
      if (s.id == this.studentService.currentStudent.id) {
        isOwner = true;
      }
    });

    if (!isOwner) {
      window.alert("Devi essere owner della VM per modificarne le risorse");
      return;
    }

    if (vm.status == "off") {
      window.alert("La VM deve essere accesa per apportare modifiche");
      return;
    }

    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        id: 1,
        title: 'Edit',
        vm: vm
    };

    this.dialog.open(ManageVmContComponent, dialogConfig);
  }

  swithOnOffVm(vm: VMOwners) {
    let isOwner: boolean = false;

    vm.owners.forEach(s => {
      if (s.id == this.studentService.currentStudent.id) {
        isOwner = true;
      }
    });

    if (!isOwner) {
      window.alert("Devi essere owner della VM per modificarne lo stato");
      return;
    }

    if (vm.status == "on") {
      this.disable.emit(vm.id);
    }
    else {
      this.activate.emit(vm.id);
    }
  }

  getStatus(status: string) {
    if (status == "on") {
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

  openCreateVmsDialog() {
    const dialogRef = this.dialog.open(CreateVmsContComponent,{ id: 'dialogCreateVms'});

    //dialogRef.afterClosed().subscribe(result => {
      //console.log(`Dialog result: ${result}`);
    //});
  }

}
