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

  selectedPhoto: File;
  private teacherSelected: Teacher;
  private teachersToAdd: Teacher[] = [];

  @Input() allTeachers: Teacher[] = [];
  @Output('add') add = new EventEmitter<any>();

  constructor(private cont: AddCourseContComponent,
     private formBuilder: FormBuilder) { 

      this.AddCourseForm = this.formBuilder.group({
        name : new FormControl('', [Validators.required, Validators.minLength(3)]),
        acronym : new FormControl('', [Validators.required, Validators.minLength(2)]),
        max_iscrizioni : new FormControl('', [Validators.required, Validators.min(1), Validators.max(10)]),
        min_iscrizioni : new FormControl('', [Validators.required, Validators.min(1), Validators.max(10)]),
      });

      this.AddCourseForm.setValue({
        name: "",
        acronym: "",
        max_iscrizioni : 4,
        min_iscrizioni : 2
      });
     }

  ngOnInit(): void {
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
      (option.id != localStorage.getItem('currentId')) &&
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
      this.teachersToAdd.push(this.teacherSelected);
      this.dataSource = new MatTableDataSource<Teacher>(this.teachersToAdd);
      this.dataSource.sort = this.sort;
      this.tableVisibility = true;
    }
  }

  deleteTeacher(teacher: Teacher) {
    if (teacher != null && this.teachersToAdd.includes(teacher)) {
      this.teachersToAdd.splice(this.teachersToAdd.indexOf(teacher));
      this.dataSource = new MatTableDataSource<Teacher>(this.teachersToAdd);
      this.dataSource.sort = this.sort;

      if (this.teachersToAdd.length == 0) {
        this.tableVisibility = false;
      }
    }
  }

  addVMImage(image) {
    this.selectedPhoto = image.target.files[0];
  }

  addCourse(name: string, acronym: string, min: number, max: number) {
    if (this.selectedPhoto == null || name == null || acronym == null || min == null || max == null) {
      window.alert("Controllare di aver inserito tutti i dati richiesti e riprovare");
      return;
    }
    else if (!this.AddCourseForm.valid) {
      window.alert("Controllare che i dati inseriti rispettino tutti i vincoli");
      return;
    }

    let course = new Course(name.toLowerCase().split(' ').join('-'), acronym, min, max, 1, 4, 100, 8, 10, 10);
    
    this.add.emit({course: course, file: this.selectedPhoto, ids: this.teachersToAdd.map(t => t.id)});
  }

}
