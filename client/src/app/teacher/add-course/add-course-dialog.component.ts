import { Component, OnInit, ViewChild, Input, Output, EventEmitter } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Course } from 'src/app/models/course.model';
import { CourseService } from 'src/app/services/course.service';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { Observable, BehaviorSubject } from 'rxjs';
import { startWith, map } from 'rxjs/operators';
import { Teacher } from 'src/app/models/teacher.model';
import { AddCourseContComponent } from './add-course-cont.component';
import { FormControl, Validators, FormGroup, FormBuilder } from '@angular/forms';

@Component({
  selector: 'app-add-course-dialog',
  templateUrl: './add-course-dialog.component.html',
  styleUrls: ['./add-course-dialog.component.css']
})
export class AddCourseDialogComponent implements OnInit {
  @ViewChild('table') table: MatTable<Element>;
  @ViewChild(MatSort, {static: true}) sort: MatSort;

  AddCourseForm: FormGroup;
  displayedColumns: string[] = ['id', 'name', 'firstName', 'delete'];
  dataSource = new MatTableDataSource<Teacher>();
  tableVisibility: boolean = false;

  myControl = new FormControl();
  filteredOptions: Observable<Teacher[]>;

  @Input() allTeachers: Teacher[] = [];
  teacherSelected: Teacher;
  @Output('teachers') teachersToAdd: BehaviorSubject<Teacher[]> = new BehaviorSubject<Teacher[]>([]);
  @Output('add') add: EventEmitter<Course> = new EventEmitter<Course>();

  constructor(private cont: AddCourseContComponent,
     private courseService: CourseService,
     private formBuilder: FormBuilder) { 

      this.AddCourseForm = this.formBuilder.group({
        name : new FormControl('', [Validators.required, Validators.minLength(3)]),
        acronym : new FormControl('', [Validators.required, Validators.minLength(2)]),
        max_iscrizioni : new FormControl('', [Validators.required, Validators.min(10),Validators.max(250)]),
        min_iscrizioni : new FormControl('', [Validators.required, Validators.min(10),Validators.max(250)]),
      });
     }

  ngOnInit(): void {
    this.dataSource.sort = this.sort;
    this.filteredOptions = this.myControl.valueChanges
      .pipe(
        startWith(''),
        map(value => typeof value === 'string' ? value : value.name),
        map(value => this._filter(value))
      );
  }

  close() {
    this.cont.close();
  }

  _filter(value: string): Teacher[] {
    const filterValue = value.toLowerCase();

    return this.allTeachers.filter(option => 
      (option.name.toString().toLowerCase().includes(filterValue) || option.firstName.toString().toLowerCase().includes(filterValue)));
  }

  displayFn(teacher: Teacher) {
    if (teacher != null)
      return teacher.name.concat(" ", teacher.firstName, " (", teacher.id, ")");
    else
      return "";
  }

  onTeacherSelected(teacher: Teacher) {
    this.teacherSelected = teacher;
  }

  addTeacher() {
    if (this.teacherSelected != null && !this.teachersToAdd.getValue().includes(this.teacherSelected)) {
      this.teachersToAdd.getValue().push(this.teacherSelected);
      this.dataSource = new MatTableDataSource<Teacher>(this.teachersToAdd.getValue());
      this.tableVisibility = true;
    }
  }

  deleteTeacher(teacher: Teacher) {
    if (teacher != null && this.teachersToAdd.getValue().includes(teacher)) {
      this.teachersToAdd.getValue().splice(this.teachersToAdd.getValue().indexOf(teacher));
      this.dataSource = new MatTableDataSource<Teacher>(this.teachersToAdd.getValue());

      if (this.teachersToAdd.getValue().length == 0) {
        this.tableVisibility = false;
      }
    }
  }

  addCourse(name: string, acronym: string, min: number, max: number) {
    let course = new Course(name.toLowerCase().split(' ').join('-'), acronym, min, max, 0, 4, 100, 8, 10, 10);
    
    this.add.emit(course);
  }

}
