import { Component, Input, Output, OnInit, EventEmitter, ViewChild } from '@angular/core';
import { FormControl, Validators, FormGroup, FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { Student } from 'src/app/models/student.model';
import { VMOwners } from 'src/app/models/vm-owners.model';
import { VM } from 'src/app/models/vm.model';

@Component({
  selector: 'app-manage-vm',
  templateUrl: './manage-vm.component.html',
  styleUrls: ['./manage-vm.component.css']
})
export class ManageVmComponent implements OnInit {
  @ViewChild('table') table: MatTable<Element>;
  @ViewChild(MatSort, {static: true}) sort: MatSort;

  displayedColumns: string[] = ['id', 'name', 'firstName', 'delete'];
  dataSource = new MatTableDataSource<Student>();
  myControl = new FormControl();
  filteredOptions: Observable<Student[]>;
  private studentSelected: Student;
  studentsToAdd: Student[] = [];

  @Input() studentsInTeam: Student[] = [];
  @Input() vm: VMOwners;
  @Output('update') update = new EventEmitter<any>();
  @Output('delete') delete = new EventEmitter<void>();

  ModelVmForm: FormGroup;

  constructor(private formBuilder: FormBuilder,
              private dialogRef: MatDialogRef<ManageVmComponent>) { }

  ngOnInit(): void {
    this.ModelVmForm = this.formBuilder.group({
      name : new FormControl('', [Validators.required]),
      vcpu : new FormControl('', [Validators.required, Validators.min(1),Validators.max(24)]),
      disk : new FormControl('', [Validators.required, Validators.min(10),Validators.max(500)]),
      ram : new FormControl('', [Validators.required, Validators.min(1),Validators.max(250)])
    });
  
    this.ModelVmForm.setValue({
      name: this.vm.nameVM,
      vcpu: this.vm.numVcpu,
      disk: this.vm.diskSpace,
      ram: this.vm.ram
    });

    this.filteredOptions = this.myControl.valueChanges
      .pipe(
        startWith(''),
        map(value => typeof value === 'string' ? value : value.name),
        map(value => this._filter(value))
      );

      this.dataSource = new MatTableDataSource<Student>(this.vm.owners);
      this.dataSource.sort = this.sort;
  }

  _filter(value: string): Student[] {
    const filterValue = value.toLowerCase();

    return this.studentsInTeam.filter(option => 
      (!this.vm.owners.map(s => s.id).includes(option.id)) &&
      (option.name.toString().toLowerCase().includes(filterValue) || option.firstName.toString().toLowerCase().includes(filterValue)));
  }

  displayFn(student: Student) {
    if (student != null && student.name != null)
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
    }
  }

  deleteStudent(student: Student) {
    if (student != null && this.studentsToAdd.includes(student)) {
      this.studentsToAdd.splice(this.studentsToAdd.indexOf(student), 1);
      this.dataSource = new MatTableDataSource<Student>(this.vm.owners.concat(this.studentsToAdd));
      this.dataSource.sort = this.sort;
    }
  }

  editVM(name: string, vcpu: number, disk: number, ram: number) {
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
