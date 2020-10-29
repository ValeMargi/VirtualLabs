import { MatDatepicker } from '@angular/material/datepicker';
import { Component, OnInit, Input,Output,EventEmitter, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators, FormBuilder } from '@angular/forms';
import { DateAdapter } from '@angular/material/core';
import { MatSort } from '@angular/material/sort';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import * as moment from 'moment';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { Course } from 'src/app/models/course.model';
import { Student } from 'src/app/models/student.model';
import { CourseService } from 'src/app/services/course.service';

@Component({
  selector: 'app-request-team-dialog',
  templateUrl: './request-team-dialog.component.html',
  styleUrls: ['./request-team-dialog.component.css']
})
export class RequestTeamDialogComponent implements OnInit, OnChanges {
  @ViewChild('table') table: MatTable<Element>;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatDatepicker) datepicker: MatDatepicker<Date>;

  CreateTeamForm: FormGroup;
  min: number;
  max: number;

  displayedColumns: string[] = ['id', 'name', 'firstName', 'delete'];
  dataSource = new MatTableDataSource<Student>();
  tableVisibility: boolean = false;

  myControl = new FormControl();
  filteredOptions: Observable<Student[]>;


  dateTimeout: Date;

  selectedPhoto: File;
  private studentSelected: Student;
  studentsToAdd: Student[] = [];

  @Input() querying: boolean;
  @Input() course: Course;
  @Input() availableStudents: Student[] = [];
  @Output('propose') propose = new EventEmitter<any>();

  constructor(private formBuilder: FormBuilder,
              private courseService: CourseService) {
    this.CreateTeamForm = this.formBuilder.group({
      name : new FormControl('', [Validators.required]),
      //dateTimeout : new FormControl('', [Validators.required])
    });
  }

  ngOnInit() {
    //this.date = new Date();
    //this.oneWeek.setDate(this.oneWeek.getDate() + 7);
    this.setupFiter();
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
    }
  }

  setupFiter() {
    this.filteredOptions = this.myControl.valueChanges
      .pipe(
        startWith(''),
        map(value => typeof value === 'string' ? value : value.name),
        map(value => this._filter(value))
      );
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

  onStudentSelected(student: Student) {
    this.studentSelected = student;
  }

  addStudent() {
    if (this.studentSelected != null && !this.studentsToAdd.includes(this.studentSelected)) {
      this.studentsToAdd.push(this.studentSelected);
      this.dataSource = new MatTableDataSource<Student>(this.studentsToAdd);
      this.dataSource.sort = this.sort;
      this.tableVisibility = true;
      this.myControl.reset("");
      this.setupFiter();
    }
  }

  deleteStudent(student: Student) {
    if (student != null && this.studentsToAdd.includes(student)) {
      this.studentsToAdd.splice(this.studentsToAdd.indexOf(student), 1);
      this.dataSource = new MatTableDataSource<Student>(this.studentsToAdd);
      this.dataSource.sort = this.sort;

      if (this.studentsToAdd.length == 0) {
        this.tableVisibility = false;
      }

      this.setupFiter();
    }
  }

  proposeTeam(nameTeam: string, expire: Date) {
    console.log("Date:"+expire);

    if (this.studentsToAdd.length + 1 < this.min || this.studentsToAdd.length + 1> this.max) {
      window.alert("Selezionare un numero di studenti compreso tra " + this.min + " e " + this.max);
    }
    else if (this.CreateTeamForm.valid) {
      let d = expire;
      var timeout = /*d.getFullYear().toString()+"-"+
                    ((d.getMonth()+1).toString().length==2?(d.getMonth()+1).toString():"0"+(d.getMonth()+1).toString())+"-"+
                    (d.getDate().toString().length==2?d.getDate().toString():"0"+d.getDate().toString())+" "+
                    (d.getHours().toString().length==2?d.getHours().toString():"0"+d.getHours().toString())+":"+
                    ((parseInt(d.getMinutes()/5)*5).toString().length==2?(parseInt(d.getMinutes()/5)*5).toString():"0"+(parseInt(d.getMinutes()/5)*5).toString())+":00"+
                    parseInt(d.getMilliseconds()).toString();*/

                    d
                    .toISOString() // Convert date to a string in the format of 2019-03-25T00:07:22.0253Z
                    .substr(0, 19)  // Strip off the milliseconds and Zulu timezone indication
                    .replace('T', ' '); // Replace the T for "time" with a space



                    var date = expire.getFullYear() + '-' + (expire.getMonth() + 1) + '-' + expire.getDate();
                    var time = expire.getHours() + ":" +
                               (expire.getMinutes() < 10 ? '0' : '') +
                               expire.getMinutes() + ":" +
                               expire.getSeconds()+"."+
                               expire.getMilliseconds();
                    var timeout = date + ' ' + time;

      console.log(timeout);
      let membersId: string[] = this.studentsToAdd.map(s => s.id);
      this.propose.emit({teamName: nameTeam, timeout: timeout, membersId: membersId});
    }
    else {
      window.alert("Controllare di aver inserito dei campi validi e riprovare");
    }
  }

}


//console.log(timeout);
      /*let res = expire.split("-");
      let date = new Date(Number.parseInt(res[0]), Number.parseInt(res[1]) - 1, Number.parseInt(res[2]), 23, 59, 59, 999);
      let timeout = moment(date).format("YYYY-MM-DD HH:mm:ss.SSS"); 2020-10-29 12:55:00322*/



       //let num = new Date().setDate(new Date().getDate() + Number.parseInt(expire));
      //console.log(num);
      //let timeout: string = (num.format("YYYY-MM-DD HH:mm:ss.SSS"));

      //let res = expire.split('-');
      //let date = new Date(Number.parseInt(res[0]), Number.parseInt(res[1]) - 1, Number.parseInt(res[2]), 23, 59, 59, 999);
      //let timeout = moment(date).format("YYYY-MM-DD HH:mm:ss.SSS");
