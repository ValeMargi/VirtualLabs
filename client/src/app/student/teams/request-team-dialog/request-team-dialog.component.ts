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
  
  //seleziona tutti gli studenti nella pagina correnti della tabella
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
  }

  //seleziona/deseleziona un singolo studente
  selectStudent(isChecked, row) {
    if (isChecked) {
      this.selectedStudents.select(row);
    }
    else {
      this.selectedStudents.deselect(row);
    }
  }

  //seleziona tutti gli studenti di tutte le pagine
  selectAll(){
    this.dataSource.data.forEach(s => this.selectStudent(true, s));
  }

  //ritorna true se tutti gli studenti della pagina corrente sono selezionati, false viceversa
  currentItemsAllSelected() {
    if (!this.selectedStudents.hasValue()) {
      return false;
    }

    if (this.selectedStudents.selected.length == this.availableStudents.length) {
      return true;
    }

    let allChecked: boolean = true;
    let initIndex = this.pageSize*this.pageIndex;
    let selected = this.selectedStudents.selected;

    if (initIndex < 0 || initIndex >= this.availableStudents.length || selected == undefined) {
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

  //ritorna true se almeno uno studente della pagina corrente è selezionato, false se nessuno è selezionato
  currentItemsSelected() {
    if (this.currentItemsAllSelected() || this.selectedStudents.selected.length == this.availableStudents.length || !this.selectedStudents.hasValue() || this.selectedStudents.selected.length == 0) {
      return false;
    }

    let checked: boolean = false;
    let initIndex = this.pageSize*this.pageIndex;
    let selected = this.selectedStudents.selected;

    if (initIndex < 0 || initIndex >= this.availableStudents.length || selected == null) {
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

  //aggiornamento informazioni relative al mat paginator
  onPageChanged(event) {
    this.previousPageIndex = event.previousPageIndex;
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
  }

  //ritorna true se tutti gli studenti di tutte le pagine sono selezionati, false viceversa
  allSelected() {
    return this.dataSource.data.length == this.selectedStudents.selected.length;
  }

  proposeTeam(nameTeam: string, expire: string) {
    const studentsToAdd = this.selectedStudents.selected;
    //gli studenti selezionati devono stare nel range min-max
    if (studentsToAdd.length + 1 < this.min || studentsToAdd.length + 1> this.max) {
      window.alert("Selezionare un numero di studenti compreso tra " + this.min + " e " + this.max);
    }
    else if (this.CreateTeamForm.valid) {
      let res = expire.split("-");
      const date = new Date(Number.parseInt(res[0]), Number.parseInt(res[1]) - 1, Number.parseInt(res[2]), 23, 59, 59, 999);
      const timeout = moment(date).format("YYYY-MM-DD HH:mm:ss.SSS");

      let membersId: string[] = studentsToAdd.map(s => s.id);
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

