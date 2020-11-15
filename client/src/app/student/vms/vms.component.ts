import { AuthService } from 'src/app/auth/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
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
import { ViewImageContComponent } from 'src/app/view-image/view-image-cont/view-image-cont.component';
import { Subscription } from 'rxjs';
@Component({
  selector: 'app-vms-student',
  templateUrl: './vms.component.html',
  styleUrls: ['./vms.component.css']
})
export class VmsComponent implements AfterViewInit, OnInit, OnChanges {
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

  displayedColumns: string[] = ['vmName', 'owners', 'status', 'link', 'swithOnOff', 'edit'];
  dataSource = new MatTableDataSource<VMOwners>();
  @Input() vms: VMOwners[];
  @Input() team: Team;
  @Input() members: Student[];

  @Output('activate') activate = new EventEmitter<number>();
  @Output('disable') disable = new EventEmitter<number>();

  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  vmVisibility: boolean = true;
  noTeamVisibility: boolean = true;

  routeQueryParams$: Subscription;

  constructor(private studentService: StudentService,
              public dialog: MatDialog,
              private router: Router,
              private route: ActivatedRoute,
              private authService: AuthService) { }

  ngAfterViewInit(): void {

  }

  ngOnInit() {
    this.manageViews();

    this.routeQueryParams$ = this.route.queryParams.subscribe(params => {
      if (params['createVm']) {
        this.openCreateVmsDialog();
      }
      else if (params['editVm']) {
        this.openDialogEdit(history.state.vm);
      }
    });
  }

  routeCreateVm() {
    this.router.navigate([], {queryParams: {createVm : "true"}});
  }

  routeToEditVm(vm: VMOwners) {
    this.router.navigate([], {queryParams: {editVm : "true"}, state: {vm: vm}});
  }

  openCreateVmsDialog() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    const dialogRef = this.dialog.open(CreateVmsContComponent,{ id: 'dialogCreateVms'});

    dialogRef.componentInstance.TEAM = this.team;

    dialogRef.afterClosed().subscribe(result => {
      const queryParams = {}
      this.router.navigate([], { queryParams, replaceUrl: true, relativeTo: this.route });
    });
  }

  ngOnDestroy() {
    this.routeQueryParams$.unsubscribe();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.team != null) {
      this.team = changes.team.currentValue;
    }

    if (changes.members != null) {
      this.members = changes.members.currentValue;
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
    this.setDataSourceAttributes();
    this.length = this.vms.length;
  }

  setDataSourceAttributes() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  openDialogEdit(vm: VMOwners) {
    if (vm == null) {
      //se si ricarica la pagine, la vm non rimane nell'history state
      const queryParams = {}
      this.router.navigate([], { queryParams, replaceUrl: true, relativeTo: this.route });
      return;
    }

    let isOwner: boolean = false;

    vm.owners.forEach(s => {
      if (s.id == this.studentService.currentStudent.id) {
        isOwner = true;
      }
    });

    if (!isOwner) {
      window.alert("Devi essere owner della VM per modificarne le risorse");
      const queryParams = {}
      this.router.navigate([], { queryParams, replaceUrl: true, relativeTo: this.route });
      return;
    }

    if (vm.status == "on") {
      window.alert("La VM deve essere spenta per apportare modifiche");
      const queryParams = {}
      this.router.navigate([], { queryParams, replaceUrl: true, relativeTo: this.route });
      return;
    }

    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;

    dialogConfig.data = {
        id: 1,
        title: 'Edit',
        vm: vm,
        members: this.members
    };

    const dialogRef = this.dialog.open(ManageVmContComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(result => {
      if (dialogRef.componentInstance.updated) {
        const vmNew: VMOwners = dialogRef.componentInstance.VM;
        let index: number = this.vms.map(vm => vm.id).indexOf(vm.id);
        this.vms[index] = vmNew;
        this.manageTable();
      }

      const queryParams = {}
      this.router.navigate([], { queryParams, replaceUrl: true, relativeTo: this.route });
    });
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

  //ritorna la string corrispondente allo stato
  getStatus(status: string) {
    if (status == "on") {
      return "Accesa";
    }
    else {
      return "Spenta";
    }
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
        isTeacher: false
    };

    this.dialog.open(ViewImageContComponent, dialogConfig);
  }



}
