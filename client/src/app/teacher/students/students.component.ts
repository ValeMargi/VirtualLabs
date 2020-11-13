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
import { StudentGroup } from 'src/app/models/student-group.model';



@Component({
  selector: 'app-students',
  templateUrl: './students.component.html',
  styleUrls: ['./students.component.css']
})
export class StudentsComponent implements AfterViewInit, OnInit, OnChanges {
  @ViewChild('table') table: MatTable<Element>;
  @ViewChild('sidenav') sidenav: MatSidenav;
  @ViewChild('checkall') checkall: MatCheckbox ;
  @ViewChild('checksingle') checksingle: MatCheckbox;
  @ViewChild('input') input: MatInput

  private sort: MatSort;
  private paginator: MatPaginator;
  selection: any;
  selectionAmount: any;
  checkBoxAll: boolean = false;

  @ViewChild(MatSort) set matSort(ms: MatSort) {
    this.sort = ms;
    this.setDataSourceAttributes();
  }

  @ViewChild(MatPaginator) set matPaginator(mp: MatPaginator) {
    this.paginator = mp;
    this.setDataSourceAttributes();
  }

  @Input() students: StudentGroup[];
  @Input() options: Student[];
  @Output('enroll') toInsert = new EventEmitter<Student>()
  @Output('CSV') toInsertCSV = new EventEmitter<File>();
  @Output('remove') toRemove = new EventEmitter<StudentGroup[]>()

  displayedColumns: string[] = ['select', 'id', 'name', 'firstName', 'team'];
  dataSource = new MatTableDataSource<StudentGroup>();
  selectedStudents = new SelectionModel<StudentGroup>(true, []);

  myControl = new FormControl();
  selectedCSV: File;

  filteredOptions: Observable<Student[]>;
  studentToAdd: Student = null;

  pageEvent: boolean;

  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];
  pageIndex: number = 0;
  previousPageIndex: number;
  totalPages: number;

  tableVisibility: boolean = true;
  addDisabled: boolean = true;

  checkBoxMaster: MatCheckbox;


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
    }

    if (changes.options != null) {
      this.options = changes.options.currentValue;
    }

    this.setupFilter();
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
    this.dataSource = new MatTableDataSource<StudentGroup>(this.students);
    this.setDataSourceAttributes();
    this.length = this.students.length;
  }

  setDataSourceAttributes() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
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

  selectStudentOnCurrenPage(isChecked) {
    let initIndex = this.pageSize*this.pageIndex;

    if (!isChecked) {
      this.selectedStudents.clear();
    }
    else {
      const remaining = this.dataSource.data.length - initIndex;
      const limit = (remaining >= this.pageSize) ? this.pageSize : remaining;
      for (let i = initIndex; i < initIndex + limit; i++) {
        this.selectStudent(true, this.dataSource.data[i]);
      }
    }

    if (this.currentItemsAllSelected() && !this.allSelected()) {
      this.checkBoxAll = true
    }
    else {
      this.checkBoxAll = false;
    }
  }

  selectStudent(isChecked, row) {
    if (isChecked) {
      this.selectedStudents.select(row);
    }
    else {
      this.selectedStudents.deselect(row);
    }

    if (this.currentItemsAllSelected() && !this.allSelected()) {
      this.checkBoxAll = true
    }
    else {
      this.checkBoxAll = false;
    }
  }

  selectAll(){
    this.checkBoxAll = false;
    this.dataSource.data.forEach(s => this.selectStudent(true, s));
  }

  currentItemsAllSelected() {
    if (!this.selectedStudents.hasValue()) {
      return false;
    }

    if (this.selectedStudents.selected.length == this.students.length) {
      return true;
    }

    let allChecked: boolean = true;
    let initIndex = this.pageSize*this.pageIndex;
    let selected = this.selectedStudents.selected;

    if (initIndex < 0 || initIndex >= this.students.length || selected == undefined) {
      return false;
    }

    const remaining = this.dataSource.data.length - initIndex;
    const limit = (remaining >= this.pageSize) ? this.pageSize : remaining;
    for (let i = initIndex; i < initIndex + limit; i++) {
      if(!selected.includes(this.dataSource.data[i])){
        allChecked = false;
      }
    }

    return allChecked;
  }

  currentItemsSelected() {
    if (this.currentItemsAllSelected() || this.selectedStudents.selected.length == this.students.length || !this.selectedStudents.hasValue() || this.selectedStudents.selected.length == 0) {
      return false;
    }

    let checked: boolean = false;
    let initIndex = this.pageSize*this.pageIndex;
    let selected = this.selectedStudents.selected;

    if (initIndex < 0 || initIndex >= this.students.length || selected == null) {
      return false;
    }

    const remaining = this.dataSource.data.length - initIndex;
    const limit = (remaining >= this.pageSize) ? this.pageSize : remaining;
    for (let i = initIndex; i < initIndex + limit; i++) {
      if (selected.includes(this.dataSource.data[i])){
        checked = true;
      }
    }

    return checked;
  }

  onPageChanged(event) {
    this.previousPageIndex = event.previousPageIndex;
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
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
    if (student != null && student.name != null && student.firstName != null)
      return student.name.concat(" ", student.firstName, " (", student.id, ")");
    else
      return "";
  }

  onStudentSelected(student: Student) {
    this.studentToAdd = student;
    this.addDisabled = false;
  }


}
