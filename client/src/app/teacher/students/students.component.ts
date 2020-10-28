import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { ViewChild, AfterViewInit } from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {MatTable} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';
import { MatSidenav } from '@angular/material/sidenav';

import { MatCheckbox } from '@angular/material/checkbox';
import { FormControl } from '@angular/forms';
import {Observable} from 'rxjs';

import {map, startWith} from 'rxjs/operators';
import { SelectionModel } from '@angular/cdk/collections';
import { MatInput } from '@angular/material/input';
import { Student } from '../../models/student.model';
import { StudentsContComponent } from './students-cont.component';



@Component({
  selector: 'app-students',
  templateUrl: './students.component.html',
  styleUrls: ['./students.component.css']
})
export class StudentsComponent implements AfterViewInit, OnInit, OnChanges {
  @ViewChild('table') table: MatTable<Element>;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild('sidenav') sidenav: MatSidenav;
  @ViewChild('checkall') checkall: MatCheckbox;
  @ViewChild('checksingle') checksingle: MatCheckbox;
  @ViewChild('input') input: MatInput

  @Input() students: Student[];
  @Input() options: Student[];
  @Output('enroll') toInsert = new EventEmitter<Student>()
  @Output('CSV') toInsertCSV = new EventEmitter<File>();
  @Output('remove') toRemove = new EventEmitter<Student[]>()

  displayedColumns: string[] = ['select', 'id', 'name', 'firstName', 'team'];
  dataSource = new MatTableDataSource<Student>();
  selectedStudents = new SelectionModel<Student>(true, []);

  myControl = new FormControl();
  selectedCSV: File;

  filteredOptions: Observable<Student[]>;
  studentToAdd: Student = null;

  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  tableVisibility: boolean = true;
  addDisabled: boolean = true;

  constructor() {}

  ngAfterViewInit(): void {
    this.setTable();
  }

  ngOnInit() {
    this.setTable();
    this.studentToAdd = null;
    this.setupFilter();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.students != null) {
      this.students = changes.students.currentValue;
      this.setupFilter();
    }

    if (changes.options != null) {
      this.options = changes.options.currentValue;
    }

    this.manageTableVisibility();
    this.setTable();
  }

  manageTableVisibility() {
    if (this.students.length > 0) {
      this.tableVisibility = true;
    }
    else {
      this.tableVisibility = false;
    }
  }

  setTable() {
    this.dataSource = new MatTableDataSource<Student>(this.students);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.length = this.students.length;
  }

  setupFilter() {
    this.filteredOptions = this.myControl.valueChanges
      .pipe(
        startWith(''),
        map(value => typeof value === 'string' ? value : value.name),
        map(value => this._filter(value)));
  }

  private _filter(value: string): Student[] {
    const filterValue = value.toLowerCase();

    return this.options.filter(option =>
      (!this.students.map(s => s.id).includes(option.id)) &&
      (option.name.toString().toLowerCase().includes(filterValue) || option.firstName.toString().toLowerCase().includes(filterValue)));
  }

  selectStudent(isChecked, row) {
    if (isChecked) {
      this.selectedStudents.select(row);

      if (this.allSelected()) {
        this.checkall.checked = true;
        this.checkall.indeterminate = false;
      }
      else {
        this.checkall.indeterminate = true;
        this.checkall.checked = false;
      }
    }
    else {
      this.selectedStudents.deselect(row);

      if (this.selectedStudents.selected.length == 0) {
        this.checkall.indeterminate = false;
      }
      else {
        this.checkall.indeterminate = true;
      }

      this.checkall.checked = false;
    }
  }

  selectAll(isChecked) {
    this.dataSource.data.forEach(s => this.selectStudent(isChecked, s));
  }

  allSelected() {
    return this.dataSource.data.length == this.selectedStudents.selected.length;
  }

  onSearchChange(searchValue: string) {
    if (searchValue.length > 0) {
      this.addDisabled = false;
    }
    else {
      this.addDisabled = true;
    }
  }

  deleteStudent() {
    if (this.selectedStudents.selected.length > 0) {
      this.toRemove.emit(this.selectedStudents.selected);
      this.dataSource._updateChangeSubscription();
      this.selectedStudents.clear();
    }
  }

  addStudent() {
    if (this.studentToAdd != null) {

      var add = true;
      this.students.forEach(student => {
        if (student.id == this.studentToAdd.id)
          add = false;
      });

      if (add) {
        this.toInsert.emit(this.studentToAdd);
        this.myControl.reset("");
      }
    }
  }

  addStudentCSV(file) {
    this.selectedCSV = file.target.files[0]
    this.toInsertCSV.emit(this.selectedCSV);
  }

  displayFn(student: Student) {
    if (student != null && student.name != null)
      return student.name.concat(" ", student.firstName, " (", student.id, ")");
    else
      return "";
  }

  onStudentSelected(student: Student) {
    this.studentToAdd = student;
    this.addDisabled = false;
  }

}
