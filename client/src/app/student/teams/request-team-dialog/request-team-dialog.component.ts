import { Component, OnInit, Input,Output,EventEmitter, OnChanges,SimpleChanges } from '@angular/core';
import { ViewChild, AfterViewInit } from '@angular/core';
import { MatTable } from '@angular/material/table';
import { FormControl, Validators } from '@angular/forms';
import { Student } from '../../../models/student.model';
import { Observable } from 'rxjs';
import { map, startWith} from 'rxjs/operators';
import { SelectionModel } from '@angular/cdk/collections';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatInput } from '@angular/material/input';
import { MatTableDataSource } from '@angular/material/table';
import { CourseService } from 'src/app/services/course.service';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-request-team-dialog',
  templateUrl: './request-team-dialog.component.html',
  styleUrls: ['./request-team-dialog.component.css']
})
export class RequestTeamDialogComponent implements AfterViewInit,OnInit,OnChanges{
  @ViewChild('table') table: MatTable<Element>;
  @ViewChild('checkall') checkall: MatCheckbox;
  @ViewChild('checksingle') checksingle: MatCheckbox;

  @Input() students: Student[];
  @Input() options: Student[];

  @Input() STUDENTS_ENROLLED: Student[] = []
  @Input() ALL_STUDENTS: Student[] = []

  @Output('enroll') toInsert = new EventEmitter<Student>()
  @Output('remove') toRemove = new EventEmitter<Student[]>()

  displayedColumns: string[] = ['select','matricola', 'cognome', 'nome'];
  dataSource = new MatTableDataSource<Student>();
  selectedStudents = new SelectionModel<Student>(true, []);
  myControl = new FormControl();
  studentToAdd : Student = null;
  filteredOptions: Observable<Student[]>;
  addDisabled: boolean = true;

  form = {
    name : new FormControl('', [Validators.required, Validators.minLength(3)]),
    date : new FormControl('', [Validators.required])
  }

  constructor(
    private courseService: CourseService,
    private router: Router,
    private route: ActivatedRoute) {}

  ngAfterViewInit(): void {}

  ngOnInit() {
    this.setTable();
    this.studentToAdd = null;
    this.selectedStudents.clear();
    this.filteredOptions = this.myControl.valueChanges
      .pipe(
        startWith(''),
        map(value => typeof value === 'string' ? value : value.name),
        map(value => this._filter(value)));
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.students != null) {
      this.students = changes.students.currentValue;
    }
    else {
      this.students = new Array();
    }

    this.setTable();
  }

  enrollStudent(student: Student) {
    this.courseService.enrollOne(this.courseService.currentCourse.getValue().name, student.id).subscribe(
      (data) => {
        this.STUDENTS_ENROLLED = this.STUDENTS_ENROLLED.concat(student);
      },
      (error) => {
        console.log("studente non aggiunto");
      }
      );
  }

  removeStudents(students: Student[]) {
    this.courseService.deleteStudentsFromCourse(this.courseService.currentCourse.getValue().name, students.map(student => student.id)).subscribe(
      (data) => {
        data.forEach(student => {
          this.STUDENTS_ENROLLED.forEach(s => {
            if (s.id == student.id) {
              this.STUDENTS_ENROLLED.splice(this.STUDENTS_ENROLLED.indexOf(s));
            }
          })
        });
      },
      (error) => {
        console.log("Rimozione studenti non avvenuta");
       }
    );
  }

  getErrorMessage() {
    if (this.form.name.hasError('required') || this.form.date.hasError('required')) {
      return 'Campo obbligatorio';
    }
    if(this.form.name.hasError('minlength')){
      return 'Inserire almeno 3 caratteri';
    }
  }

  setTable() {
    this.dataSource = new MatTableDataSource<Student>(this.students);
  }

  private _filter(value: string): Student[] {
    const filterValue = value.toLowerCase();

    return this.options.filter(option =>
      (option.name.toString().toLowerCase().includes(filterValue) || option.firstName.toString().toLowerCase().includes(filterValue)));
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


selectAll(isChecked) {
  this.dataSource.data.forEach(s => this.selectStudent(isChecked, s));
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
  }
}

addStudent() {
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


}
