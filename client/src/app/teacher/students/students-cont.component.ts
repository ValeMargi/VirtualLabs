import { Component, EventEmitter, OnInit, Output, Inject, AfterViewInit } from '@angular/core';
import { Student } from '../../models/student.model';
import { StudentService } from '../../services/student.service';
import { AuthService } from '../../auth/auth.service';
import { CourseService } from 'src/app/services/course.service';


@Component({
  selector: 'app-students-cont',
  templateUrl: './students-cont.component.html',
  styleUrls: ['./students-cont.component.css']
})
export class StudentsContComponent implements OnInit, AfterViewInit {

  STUDENTS_ENROLLED: Student[] = []
  ALL_STUDENTS: Student[] = []

                              
  constructor(private studentService: StudentService,
              private courseService: CourseService) { 
    
  }

  @Output() allStudents = new EventEmitter<Student[]>()
  @Output() enrolledStudents = new EventEmitter<Student[]>()

  ngAfterViewInit() {

    this.studentService.all().subscribe(
      (data) => {
        this.ALL_STUDENTS = data;
        this.allStudents.emit(this.ALL_STUDENTS);
      },
      (error) => { 
        console.log("studenti non reperiti");
       } 
      );

    this.courseService.enrolledStudents(this.courseService.currentCourse.getValue().name).subscribe(
      (data) => {
        console.log(data);
        this.STUDENTS_ENROLLED = data;
        this.enrolledStudents.emit(this.STUDENTS_ENROLLED);
      },
      (error) => { 
        console.log("studenti iscritti non reperiti");
       } 
      );
    
  }

  ngOnInit(): void {
  
  }

  enrollStudent(student: Student) {
    this.courseService.enrollOne(this.courseService.currentCourse.getValue().name, student.id).subscribe(
      (success) => {
        this.STUDENTS_ENROLLED = this.STUDENTS_ENROLLED.concat(student);
        this.enrolledStudents.emit(this.STUDENTS_ENROLLED);
      },
      (error) => { 
        console.log("studente non aggiunto");
      } 
      );
  }

  removeStudents(students: Student[]) {
    this.courseService.deleteStudentsFromCourse(this.courseService.currentCourse.getValue().name, students.map(student => student.id)).subscribe(
      (success) => {
        students.forEach(student => {
          this.STUDENTS_ENROLLED.forEach(s => {
            if (s.id == student.id) {
              this.STUDENTS_ENROLLED.splice(this.STUDENTS_ENROLLED.indexOf(s), 1);
            }
          })
        });
        this.enrolledStudents.emit(this.STUDENTS_ENROLLED);
      },
      (error) => { 
        console.log("rimozione non avvenuta");
       }
    );
  }

}
