import { MatDatepicker } from '@angular/material/datepicker';
import { Component, OnInit, Input,Output,EventEmitter, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators, FormBuilder } from '@angular/forms';
import { Location } from '@angular/common';
import { DateAdapter } from '@angular/material/core';
import { MatSort } from '@angular/material/sort';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import * as moment from 'moment';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { Course } from 'src/app/models/course.model';
import { Student } from 'src/app/models/student.model';
import { CourseService } from 'src/app/services/course.service';
import { MatCheckbox } from '@angular/material/checkbox';
import { SelectionModel } from '@angular/cdk/collections';
import { MatPaginator } from '@angular/material/paginator';

@Component({
  selector: 'app-request-team-dialog',
  templateUrl: './request-team-dialog.component.html',
  styleUrls: ['./request-team-dialog.component.css']
})
export class RequestTeamDialogComponent implements OnInit, OnChanges {
  @ViewChild('table') table: MatTable<Element>;
  @ViewChild('checkall') checkall: MatCheckbox;
  @ViewChild('checksingle') checksingle: MatCheckbox;
  
  sort: MatSort;
  paginator: MatPaginator;
  
  @ViewChild(MatSort) set matSort(ms: MatSort) {
    this.sort = ms;
    this.dataSource.sort = this.sort;
  }

  @ViewChild(MatPaginator) set matPaginator(mp: MatPaginator) {
    this.paginator = mp;
    this.dataSource.paginator = this.paginator;
  }

  CreateTeamForm: FormGroup;
  min: number;
  max: number;

  displayedColumns: string[] = ['select', 'id', 'name', 'firstName'];
  dataSource = new MatTableDataSource<Student>();
  selectedStudents = new SelectionModel<Student>(true, []);

  myControl = new FormControl();
  dateControl = new FormControl(new Date());
  filteredOptions: Observable<Student[]>;
  dateTimeout: Date;
  currentDate: Date;

  studentsToAdd: Student[] = [];

  length = 5;
  pageSize = 5;
  pageSizeOptions: number[] = [5, 10, 25, 100];
  pageIndex: number = 0;
  previousPageIndex: number;

  @Input() querying: boolean;
  @Input() course: Course;
  @Input() availableStudents: Student[] = [];
  @Output('propose') propose = new EventEmitter<any>();

  constructor(private formBuilder: FormBuilder,
              private location: Location) {
    this.currentDate = new Date();
    const oneWeek = new Date(this.currentDate);
    oneWeek.setDate(oneWeek.getDate() + 7);

    this.CreateTeamForm = this.formBuilder.group({
      name : new FormControl('', [Validators.required]),
      date : new FormControl(oneWeek, [Validators.required])
    });
  }

  ngOnInit() {
    
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.querying != null) {
      this.querying = changes.querying.currentValue;
    }

    if (changes.course != null) {
      this.course = changes.course.currentValue;

      if (this.course != null) {
        this.min = this.course.min;
        this.max = this.course.max;
      }
    }

    if (changes.availableStudents != null) {
      this.availableStudents = changes.availableStudents.currentValue;
      this.dataSource = new MatTableDataSource<Student>(this.availableStudents);
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
      this.length = this.availableStudents.length;
    }
  }

  _filter(value: string): Student[] {
    const filterValue = value.toLowerCase();

    return this.availableStudents.filter(option =>
      (option.id != localStorage.getItem('currentId') && !this.studentsToAdd.includes(option)) &&
      (option.name.toString().toLowerCase().includes(filterValue) || option.firstName.toString().toLowerCase().includes(filterValue)));
  }

  displayFn(student: Student) {
    if (student != null && student.name != null && student.firstName != null)
      return student.name.concat(" ", student.firstName, " (", student.id, ")");
    else
      return "";
  }

  selectStudent(isChecked: boolean, row: Student) {
    if (isChecked) {
      this.selectedStudents.select(row);
      this.studentsToAdd.push(row);

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
      this.studentsToAdd.splice(this.studentsToAdd.indexOf(row), 1);

      if (this.selectedStudents.selected.length == 0) {
        this.checkall.indeterminate = false;
      }
      else {
        this.checkall.indeterminate = true;
      }

      this.checkall.checked = false;
    }
  }

  selectAll() {
    this.dataSource.data.forEach(s => this.selectStudent(true, s));
  }

  deselectAll() {
    this.dataSource.data.forEach(s => this.selectStudent(false, s));
  }

  currentItemsSelected(){
    let allChecked: boolean = true;
    let initIndex = this.pageSize*this.pageIndex;

    let selected = this.selectedStudents.selected;

    for(let i = initIndex; i < (initIndex + this.pageSize); i++){
      if(!selected.includes(this.dataSource.data[i])){
        allChecked = false;
      }
    }
    return allChecked;
  }

  selectCurrentPage(isChecked){
    let initIndex = this.pageSize*this.pageIndex;

    if(this.pageSize > this.length || this.previousPageIndex < this.pageIndex){
    }

    if (!isChecked) {
      this.dataSource.data.forEach(s => this.selectStudent(false, s));
    }

    for(let i = initIndex; i < (initIndex + this.pageSize); i++){
      this.selectStudent(isChecked, this.dataSource.data[i]);
    }
  }

  onPageChanged(event){
    this.previousPageIndex = event.previousPageIndex;
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
  }

  allSelected() {
    return this.dataSource.data.length == this.selectedStudents.selected.length;
  }
  

  proposeTeam(nameTeam: string, expire: string) {
    if (this.studentsToAdd.length + 1 < this.min || this.studentsToAdd.length + 1> this.max) {
      window.alert("Selezionare un numero di studenti compreso tra " + this.min + " e " + this.max);
    }
    else if (this.CreateTeamForm.valid) {
      let res = expire.split("-");
      const date = new Date(Number.parseInt(res[0]), Number.parseInt(res[1]) - 1, Number.parseInt(res[2]), 23, 59, 59, 999);
      const timeout = moment(date).format("YYYY-MM-DD HH:mm:ss.SSS");

      let membersId: string[] = this.studentsToAdd.map(s => s.id);
      this.propose.emit({teamName: nameTeam, timeout: timeout, membersId: membersId});
    }
    else {
      window.alert("Controllare di aver inserito dei campi validi e riprovare");
    }
  }

  backToTeams() {
    this.location.back();
  }
}

