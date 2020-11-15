import { Component, OnInit, ViewChild, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import { Course } from 'src/app/models/course.model';
import { CourseService } from 'src/app/services/course.service';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { Observable, BehaviorSubject } from 'rxjs';
import { startWith, map } from 'rxjs/operators';
import { Teacher } from 'src/app/models/teacher.model';
import { AddCourseContComponent } from './add-course-cont.component';
import { FormControl, Validators, FormGroup, FormBuilder, FormGroupDirective, NgForm, ValidationErrors } from '@angular/forms';
import { ManageModelContComponent } from '../vms/manage-model-cont.component';
import { ActivatedRoute, Router } from '@angular/router';
import { ErrorStateMatcher } from '@angular/material/core';

@Component({
  selector: 'app-add-course-dialog',
  templateUrl: './add-course-dialog.component.html',
  styleUrls: ['./add-course-dialog.component.css']
})
export class AddCourseDialogComponent implements OnInit, OnChanges {
  @ViewChild('table') table: MatTable<Element>;

  private sort: MatSort;
  panelOpenState: boolean;
  ModelVmForm: FormGroup;
  valueDefault: boolean;

  courseName: string;
  acronym: string;
  maxMembers: number;
  minMembers: number;
  maxVcpu: number;
  maxDisk: number;
  ram: number;
  totInstances: number;
  runningInstances: number;


  @ViewChild(MatSort) set matSort(ms: MatSort) {
    this.sort = ms;
    this.dataSource.sort = this.sort;
  }

  AddCourseForm: FormGroup;
  modelvm: Course;
  displayedColumns: string[] = ['id', 'name', 'firstName', 'delete'];
  dataSource = new MatTableDataSource<Teacher>();
  tableVisibility: boolean = false;

  myControl = new FormControl();
  filteredOptions: Observable<Teacher[]>;

  pageOneVisibility: boolean = true;
  pageTwoVisibility: boolean = false;

  matcher = new MyErrorStateMatcher();

  selectedPhoto: File;
  private teacherSelected: Teacher;
  private teachersToAdd: Teacher[] = [];

  @Input() allTeachers: Teacher[] = [];
  @Output('add') add = new EventEmitter<any>();

  constructor(private cont: AddCourseContComponent,
              private formBuilder: FormBuilder,
              private matDialog: MatDialog,
              private router: Router,
              private route: ActivatedRoute) {

      this.AddCourseForm = this.formBuilder.group({
        name : new FormControl('', [Validators.required]),
        acronym : new FormControl('', [Validators.required]),
        max_iscrizioni : new FormControl('', [Validators.required, Validators.min(1)]),
        min_iscrizioni : new FormControl('', [Validators.required, Validators.min(1)]),
      },{ validator: Validators.compose([CustomValidators.maxMemberValidator, CustomValidators.minMemberValidator])});

      this.AddCourseForm.setValue({
        name: "",
        acronym: "",
        max_membri : 4,
        min_membri : 1
      });

      this.ModelVmForm = this.formBuilder.group({
        max_vcpu : new FormControl('', [Validators.required, Validators.min(1)]),
        max_disco : new FormControl('', [Validators.required, Validators.min(1)]),
        max_ram : new FormControl('', [Validators.required, Validators.min(1)]),
        max_vm : new FormControl('', [Validators.required, Validators.min(1)]),
        max_vm_active : new FormControl('', [Validators.required, Validators.min(1)]),
        imageVM: new FormControl('', [Validators.required])
      }, {validator: this.maxVmValidator});

     }

  ngOnInit(): void {
    this.setupFilter();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.allTeachers != null) {
      this.allTeachers = changes.allTeachers.currentValue;
      this.setupFilter();
    }
  }

  close() {
    this.cont.close();
  }

  checkFirstPage(){

  }

  saveElementPageOne(){

  }

  nextPage(){
    this.pageOneVisibility = false;
    this.pageTwoVisibility = true;
  }

  backPage(){
    this.pageOneVisibility = true;
    this.pageTwoVisibility = false;
  }
  setupFilter() {
    this.filteredOptions = this.myControl.valueChanges
      .pipe(
        startWith(''),
        map(value => typeof value === 'string' ? value : value.name),
        map(value => this._filter(value))
      );
  }

  _filter(value: string): Teacher[] {
    const filterValue = value.toLowerCase();

    return this.allTeachers.filter(option =>
      (option.id != localStorage.getItem('currentId') && !this.teachersToAdd.includes(option)) &&
      (option.name.toString().toLowerCase().includes(filterValue) || option.firstName.toString().toLowerCase().includes(filterValue)));
  }

  displayFn(teacher: Teacher) {
    if (teacher != null && teacher.name != null && teacher.firstName != null)
      return teacher.name.concat(" ", teacher.firstName, " (", teacher.id, ")");
    else
      return "";
  }

  onTeacherSelected(teacher: Teacher) {
    this.teacherSelected = teacher;
  }

  maxVmValidator(group: FormGroup) {
    let max_vm: number = group.controls.max_vm.value;
    let max_vm_active: number = group.controls.max_vm_active.value;

    if(max_vm_active <= max_vm){
      return null;
    }else{
      return { ErrorVmActivated: true };
    }
  }

  addTeacher() {
    if (this.teacherSelected != null && !this.teachersToAdd.includes(this.teacherSelected)) {
      this.teachersToAdd.push(this.teacherSelected);
      this.dataSource = new MatTableDataSource<Teacher>(this.teachersToAdd);
      this.dataSource.sort = this.sort;
      this.tableVisibility = true;
      this.teacherSelected = null;
      this.myControl.reset("");
      this.setupFilter();
    }
  }

  setDefaultValue( defaultCheck: boolean){
    this.valueDefault = defaultCheck;

    if(defaultCheck){
      this.ModelVmForm.setValue({
        max_vcpu: 1,
        max_disco: 100,
        max_ram: 4,
        max_vm: 10,
        max_vm_active: 8,
        imageVM: ""
      });

      console.log("checked");
    }else{
      this.ModelVmForm.setValue({
        max_vcpu: "",
        max_disco: "",
        max_ram: "",
        max_vm: "",
        max_vm_active: "",
        imageVM: ""
      });

      console.log("Un-checked");
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

      this.setupFilter();
    }
  }

  addVMImage(image) {
    this.selectedPhoto = image.target.files[0];
  }

  addCourse(name: string, acronym: string, min: number, max: number, maxCpu: number, maxDisk: number, ram: number,totInstances: number, runnigInstance: number) {
    if(!this.AddCourseForm.valid || !this.ModelVmForm.valid){
      window.alert("Controllare che i dati inseriti rispettino tutti i vincoli");
      return;
    }

    console.log(name);
    console.log(acronym);
    console.log(min);
    console.log(max);
    console.log(maxCpu);
    console.log(maxDisk);
    console.log(ram);
    console.log(totInstances);
    console.log(runnigInstance);

    let course = new Course(name.toLowerCase().split(' ').join('-'), acronym.toUpperCase(), min, max, 1, maxCpu, maxDisk, ram, runnigInstance, totInstances );

    this.add.emit({course: course, file: this.selectedPhoto, ids: this.teachersToAdd.map(t => t.id)});
  }

  openDialogModelVM(){
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = false;

    const dialogRef = this.matDialog.open(ManageModelContComponent, dialogConfig);

    dialogRef.componentInstance.courseName = this.route.snapshot.params.courses;

    dialogRef.afterClosed().subscribe(result => {
      const queryParams = {}
      this.router.navigate([], { queryParams, replaceUrl: true, relativeTo: this.route });
    });
  }

}


export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl, form: FormGroupDirective | NgForm): boolean {

    const invalidCtrl = !!(control && control.invalid && control.dirty);

    const invalidParent = !!(
      control.parent.touched
      && control.parent.invalid
      && control.parent.hasError('ErrorVmActivated')
      && control.parent.hasError('ErrorMembersMax')
      );

    const invalidParent2 = !!(
      control.parent.touched
      && control.parent.invalid
      && control.parent.hasError('ErrorMembersMax')
      );

    const invalidParent3 = !!(
      control.parent.touched
      && control.parent.invalid
      && control.parent.hasError('ErrorMembersMin')
      );

    return (invalidParent || invalidCtrl || invalidParent2 || invalidParent3);
  }
}

export class CustomValidators {

  static minMemberValidator(group: FormGroup): ValidationErrors {
    let maxMember: number = group.controls.max_iscrizioni.value;
    let minMember: number = group.controls.min_iscrizioni.value;

    if(minMember <= maxMember){
      return null;
    }else{
      return { ErrorMembersMin: true };
    }
  }

  static maxMemberValidator(group: FormGroup): ValidationErrors {
    let maxMember: number = group.controls.max_iscrizioni.value;
    let minMember: number = group.controls.min_iscrizioni.value;

    if(maxMember >= minMember){
      return null;
    }else{
      return { ErrorMembersMax: true };
    }
  }
}
