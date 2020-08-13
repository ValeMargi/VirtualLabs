import { Component, OnInit, Input,Output,EventEmitter } from '@angular/core';
import { ViewChild, AfterViewInit } from '@angular/core';
import {MatSort} from '@angular/material/sort';
import {MatTable} from '@angular/material/table';
import {MatPaginator} from '@angular/material/paginator';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Student } from '../../../models/student.model';
import {Observable} from 'rxjs';
import { SelectionModel } from '@angular/cdk/collections';
import { MatCheckbox } from '@angular/material/checkbox';
import {MatTableDataSource} from '@angular/material/table';
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

  displayedColumns: string[] = ['Select','Matricola', 'Cognome', 'Nome'];
  dataSource = new MatTableDataSource<Student>();
  //dataSource = ELEMENT_DATA;
  myControl = new FormControl();
  studentToAdd : Student = null;
  
  filteredOptions: Observable<Student[]>;
  selectedStudents = new SelectionModel<Student>(true, []);
 
  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];

  form = {
    name : new FormControl('', [Validators.required, Validators.minLength(3)]),
    date : new FormControl('', [Validators.required])
  }
    getErrorMessage() {
      if (this.form.name.hasError('required') || this.form.date.hasError('required')) {
        return 'Campo obbligatorio';
      }
      if(this.form.name.hasError('minlength')){
        return 'Inserire almeno 3 caratteri';
      }
    }

  constructor() {}

  ngAfterViewInit(): void {
 
  }

  ngOnInit() {

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

  searchStudent(){
    
  }
  allSelected() {
    return this.dataSource.data.length == this.selectedStudents.selected.length;
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
}

