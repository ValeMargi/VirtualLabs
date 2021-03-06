import { Component, Input, Output, OnInit, EventEmitter, ViewChild, OnChanges, SimpleChanges } from '@angular/core';
import { FormControl, Validators, FormGroup, FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { Student } from 'src/app/models/student.model';
import { Team } from 'src/app/models/team.model';
import { VMOwners } from 'src/app/models/vm-owners.model';
import { VM } from 'src/app/models/vm.model';

@Component({
  selector: 'app-manage-vm',
  templateUrl: './manage-vm.component.html',
  styleUrls: ['./manage-vm.component.css']
})
export class ManageVmComponent implements OnInit, OnChanges {
  @ViewChild('table') table: MatTable<Element>;
  
  private sort: MatSort;
  
  @ViewChild(MatSort) set matSort(ms: MatSort) {
    this.sort = ms;
    this.dataSource.sort = this.sort;
  }

  displayedColumns: string[] = ['id', 'name', 'firstName', 'delete'];
  dataSource = new MatTableDataSource<Student>();
  myControl = new FormControl();
  filteredOptions: Observable<Student[]>;
  private studentSelected: Student;
  studentsToAdd: Student[] = [];

  @Input() studentsInTeam: Student[] = [];
  @Input() vm: VMOwners;
  @Input() team: Team;
  @Output('update') update = new EventEmitter<any>();
  @Output('delete') delete = new EventEmitter<void>();

  ModelVmForm: FormGroup;

  constructor(private formBuilder: FormBuilder,
              private dialogRef: MatDialogRef<ManageVmComponent>) { }

  ngOnInit(): void {
    this.ModelVmForm = this.formBuilder.group({
      name : new FormControl('', [Validators.required]),
      vcpu : new FormControl('', [Validators.required, Validators.min(1)]),
      disk : new FormControl('', [Validators.required, Validators.min(1)]),
      ram : new FormControl('', [Validators.required, Validators.min(1)])
    });
  
    this.ModelVmForm.setValue({
      name: this.vm.nameVM,
      vcpu: this.vm.numVcpu,
      disk: this.vm.diskSpace,
      ram: this.vm.ram
    });

    this.setupFilter();
    this.dataSource = new MatTableDataSource<Student>(this.vm.owners);
    this.dataSource.sort = this.sort;
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.team != null) {
      this.team = changes.team.currentValue;

      if (this.team != null) {
        this.ModelVmForm = this.formBuilder.group({
          name: new FormControl('', [Validators.required]),
          vcpu: new FormControl('', [Validators.required, Validators.min(1), Validators.max(this.team.maxVcpuLeft + this.vm.numVcpu)]),
          ram: new FormControl('', [Validators.required, Validators.min(1), Validators.max(this.team.ramLeft + this.vm.ram)]),
          disk: new FormControl('', [Validators.required, Validators.min(1), Validators.max(this.team.diskSpaceLeft + this.vm.diskSpace)])
        });

        this.ModelVmForm.setValue({
          name: this.vm.nameVM,
          vcpu: this.vm.numVcpu,
          disk: this.vm.diskSpace,
          ram: this.vm.ram
        });
      }
    }
  }

  setupFilter() {
    this.filteredOptions = this.myControl.valueChanges
      .pipe(
        startWith(''),
        map(value => typeof value === 'string' ? value : value.name),
        map(value => this._filter(value))
      );
  }

  _filter(value: string): Student[] {
    const filterValue = value.toLowerCase();

    return this.studentsInTeam.filter(option => 
      (!this.vm.owners.map(s => s.id).includes(option.id) && !this.studentsToAdd.includes(option)) &&
      (option.name.toString().toLowerCase().includes(filterValue) || option.firstName.toString().toLowerCase().includes(filterValue)));
  }

  displayFn(student: Student) {
    if (student != null && student.name != null && student.firstName != null)
      return student.name.concat(" ", student.firstName, " (", student.id, ")");
    else
      return "";
  }

  onStudentSelected(student: Student) {
    this.studentSelected = student;
  }

  addStudent() {
    if (this.studentSelected != null && !this.studentsToAdd.includes(this.studentSelected)) {
      this.studentsToAdd.push(this.studentSelected);
      this.dataSource = new MatTableDataSource<Student>(this.vm.owners.concat(this.studentsToAdd));
      this.dataSource.sort = this.sort;
      this.myControl.reset("");
      this.studentSelected = null;
      this.setupFilter();
    }
  }

  deleteStudent(student: Student) {
    if (student != null && this.studentsToAdd.includes(student)) {
      this.studentsToAdd.splice(this.studentsToAdd.indexOf(student), 1);
      this.dataSource = new MatTableDataSource<Student>(this.vm.owners.concat(this.studentsToAdd));
      this.dataSource.sort = this.sort;
      this.setupFilter();
    }
  }

  editVM(name: string, vcpu: number, disk: number, ram: number) {
    if (!this.ModelVmForm.valid) {
      window.alert("Controllare che tutti i dati rispettino i vincoli e riprovare");
      return;
    }

    this.vm.nameVM = name;
    this.vm.numVcpu = Number(vcpu);
    this.vm.diskSpace = Number(disk);
    this.vm.ram = Number(ram);

    this.update.emit({vm: this.vm, members: this.studentsToAdd});
  }

  deleteVM() {
    this.delete.emit();
  }

  close() {
    this.dialogRef.close();
  }
}
