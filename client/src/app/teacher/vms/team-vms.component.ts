import { Component, OnInit, Input, ViewChild, OnChanges, SimpleChanges } from '@angular/core';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { VM } from 'src/app/models/vm.model';
import { VMOwners } from 'src/app/models/vm-owners.model';
import { Student } from 'src/app/models/student.model';
import { ViewImageContComponent } from 'src/app/view-image/view-image-cont/view-image-cont.component';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { ThrowStmt } from '@angular/compiler';

@Component({
  selector: 'app-team-vms',
  templateUrl: './team-vms.component.html',
  styleUrls: ['./team-vms.component.css']
})
export class TeamVmsComponent implements OnInit, OnChanges {
  @ViewChild('table') table: MatTable<Element>;
  
  private sort: MatSort;
  private paginator: MatPaginator;
  
  @ViewChild(MatSort) set matSort(ms: MatSort) {
    this.sort = ms;
    this.setDataSourceAttributes();
  }

  @ViewChild(MatPaginator) set matPaginator(mp: MatPaginator) {
    this.paginator = mp;
    this.setDataSourceAttributes();
  }

  displayedColumns: string[] = ['vmName', 'owners', 'status', 'link'];
  dataSource = new MatTableDataSource<VMOwners>();
  
  @Input() vms: VMOwners[];
  @Input() resources: any;

  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10];

  VMsVisibility: boolean = false;

  constructor(private dialog: MatDialog) { }

  ngOnInit(): void {
    this.manageTable();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.vms != null) {
      this.vms = changes.vms.currentValue;
    }

    if (changes.resources != null) {
      this.resources = changes.resources.currentValue;
    }

    this.manageTable();
  }

  manageTable() {
    if (this.vms.length > 0) {
      this.VMsVisibility = true;
      this.dataSource = new MatTableDataSource<VMOwners>(this.vms);
      this.setDataSourceAttributes();
      this.length = this.vms.length;
    }
    else {
      this.VMsVisibility = false;
    }
  }

  setDataSourceAttributes() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
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

  openVM(vm: VM) {
    if (vm.status == "off") {
      window.alert("La VM deve essere accesa per essere avviata");
      return;
    }

    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        id: 1,
        title: 'Launch',
        type: 'vm',
        vmId: vm.id,
        isTeacher: true
    };

    this.dialog.open(ViewImageContComponent, dialogConfig);
  }

}
