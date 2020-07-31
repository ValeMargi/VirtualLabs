import { Component, OnInit, Input,Output,EventEmitter } from '@angular/core';
import { ViewChild, AfterViewInit } from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {MatSort} from '@angular/material/sort';
import { MatCheckbox } from '@angular/material/checkbox';
import {MatTableDataSource} from '@angular/material/table';
import {MatTable} from '@angular/material/table';
import {MatPaginator} from '@angular/material/paginator';
import { MatInput } from '@angular/material/input';
import { SelectionModel } from '@angular/cdk/collections';
import { Student } from '../../../models/student.model';
import {Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';
import { FormControl } from '@angular/forms';

/**
 * @title Request-Dialog with header, scrollable content and actions
 */


export interface ListStudent {
  Matricola: string;
  Cognome: string;
  Nome: string;
  Stato: string;
}

const ELEMENT_DATA: ListStudent[] = [
  {Matricola: 's267782', Cognome: 'Mosconi', Nome: 'Germano', Stato: 'L'},
  {Matricola: 's268882', Cognome: 'Brazorf', Nome: 'Ajeje', Stato: 'L'},
  {Matricola: 's269982', Cognome: 'Esposito', Nome: 'Mohamed', Stato: 'L'},
  {Matricola: 's265482', Cognome: 'La Barca', Nome: 'Remo', Stato: 'L'},
  {Matricola: 's265582', Cognome: 'Saolini', Nome: 'Gianmarco', Stato: 'L'},
 
];

@Component({
  selector: 'app-request-team-dialog',
  templateUrl: './request-team-dialog.component.html',
  styleUrls: ['./request-team-dialog.component.css']
})
export class RequestTeamDialogComponent implements AfterViewInit,OnInit{
  @ViewChild('table') table: MatTable<Element>;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild('checkall') checkall: MatCheckbox;
  @ViewChild('checksingle') checksingle: MatCheckbox;
  @ViewChild('input') input: MatInput;

  displayedColumns: string[] = ['Select','Matricola', 'Cognome', 'Nome'];
  @Input() public students: Student[];
  @Input() public options: Student[];
  @Output() allStudents = new EventEmitter<Student[]>()
  @Output() enrolledStudents = new EventEmitter<Student[]>()
  dataSource = new MatTableDataSource<Student>();
  selectedStudents = new SelectionModel<Student>(true, []);

  filteredOptions: Observable<Student[]>;
  studentToAdd : Student = null;

  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];
  myControl = new FormControl();

  @Output('enroll') toInsert = new EventEmitter<Student>()
  @Output('remove') toRemove = new EventEmitter<Student[]>()

  constructor(private cont: RequestTeamDialogComponent) {}

  ngAfterViewInit(): void {
    this.cont.enrolledStudents.subscribe(ss => {
      this.students = ss;
      this.dataSource = new MatTableDataSource<Student>(this.students);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      this.length = this.students.length;
      this.studentToAdd = null;
      this.selectedStudents.clear();
    });

    this.cont.allStudents.subscribe(ss => {
      this.options = ss;
      this.filteredOptions = this.myControl.valueChanges
      .pipe(
        startWith(''),
        map(value => typeof value === 'string' ? value : value.name),
        map(value => this._filter(value))
      );
    });
  }

  ngOnInit() {
    this.dataSource.sort = this.sort;
  }

  private _filter(value: string): Student[] {
    const filterValue = value.toLowerCase();

    return this.options.filter(option => 
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

  @Input() deleteStudent() {
    if (this.selectedStudents.selected.length > 0) {
      this.toRemove.emit(this.selectedStudents.selected);
    }
  }

  @Input() addStudent() {
    if (this.studentToAdd != null) {

      var add = true;

      this.students.forEach(student => {
        if (student.id == this.studentToAdd.id)
          add = false;
      });

      if (add)
        this.toInsert.emit(this.studentToAdd);
    }
  }

  displayFn(student: Student) {
    if (student != null)
      return student.name.concat(" ", student.firstName, " (", student.id, ")");
    else
      return "";
  }

  onStudentSelected(student: Student) {
    this.studentToAdd = student;
  }
}

