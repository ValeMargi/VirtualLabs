import { Component, Input, OnInit, Output, EventEmitter, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { Course } from 'src/app/models/course.model';
import { Teacher } from 'src/app/models/teacher.model';

@Component({
  selector: 'app-edit-course',
  templateUrl: './edit-course.component.html',
  styleUrls: ['./edit-course.component.css']
})
export class EditCourseComponent implements OnInit, OnChanges {
  @ViewChild('table') table: MatTable<Element>;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  EditCourseForm: FormGroup;
  displayedColumns: string[] = ['id', 'name', 'firstName', 'delete'];
  dataSource = new MatTableDataSource<Teacher>();
  tableVisibility: boolean = false;
  myControl = new FormControl();
  filteredOptions: Observable<Teacher[]>;
  private teacherSelected: Teacher;
  teachersToAdd: Teacher[] = [];

  @Input() allTeachers: Teacher[] = [];
  @Input() teachersInCourse: Teacher[] = [];
  @Input() course: Course;
  @Output('enable') enable = new EventEmitter<void>();
  @Output('delete') delete = new EventEmitter<void>();
  @Output('edit') edit = new EventEmitter<any>();

  constructor(private matDialogRef: MatDialogRef<EditCourseComponent>,
              private formBuilder: FormBuilder) { 

    this.EditCourseForm = this.formBuilder.group({
      max_iscrizioni : new FormControl('', [Validators.required, Validators.min(2), Validators.max(10)]),
      min_iscrizioni : new FormControl('', [Validators.required, Validators.min(2), Validators.max(10)]),
    });
  }

  ngOnInit(): void {
    if (this.course != undefined) {
      this.EditCourseForm.setValue({
        max_iscrizioni : this.course.max,
        min_iscrizioni : this.course.min
      });
    }

    this.filteredOptions = this.myControl.valueChanges
      .pipe(
        startWith(''),
        map(value => typeof value === 'string' ? value : value.name),
        map(value => this._filter(value))
      );
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.course != null) {
      this.course = changes.course.currentValue;

      this.EditCourseForm.setValue({
        max_iscrizioni : this.course.max,
        min_iscrizioni : this.course.min
      });
    }

    if (changes.teachersInCourse != null) {
      this.teachersInCourse = changes.teachersInCourse.currentValue;
      this.dataSource = new MatTableDataSource<Teacher>(this.teachersInCourse);
      this.dataSource.sort = this.sort;
      this.tableVisibility = true;
    }
  }

  close() {
    this.matDialogRef.close();
  }

  get status() {
    return (this.course.enabled == 0) ? "Disabilitato" : "Abilitato";
  }

  _filter(value: string): Teacher[] {
    const filterValue = value.toLowerCase();

    return this.allTeachers.filter(option => 
      (!this.teachersInCourse.map(t => t.id).includes(option.id)) &&
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
    if (this.teacherSelected != null && !this.teachersToAdd.includes(this.teacherSelected)) {
      this.teachersInCourse.push(this.teacherSelected);
      this.teachersToAdd.push(this.teacherSelected);
      this.dataSource = new MatTableDataSource<Teacher>(this.teachersInCourse);
      this.dataSource.sort = this.sort;
    }
  }

  deleteTeacher(teacher: Teacher) {
    if (teacher != null && this.teachersToAdd.includes(teacher)) {
      this.teachersInCourse.splice(this.teachersInCourse.indexOf(teacher));
      this.teachersToAdd.splice(this.teachersToAdd.indexOf(teacher));
      this.dataSource = new MatTableDataSource<Teacher>(this.teachersInCourse);
      this.dataSource.sort = this.sort;
    }
  }

  deleteCourse() {
    this.delete.emit();
  }

  enableCourse() {
    this.enable.emit();
  }

  editCourse(min: number, max: number) {
    if (this.EditCourseForm.valid) {
      this.edit.emit({min: min, max: max});
    }
    else {
      window.alert("Verificare che i valori inseriti rispettino tutti i vincoli e riprovare");
    }
  }

}