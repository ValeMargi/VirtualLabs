import { Component, EventEmitter, OnInit, Output, Inject, AfterViewInit } from '@angular/core';
import { Student } from '../../models/student.model';
import { StudentService } from '../../services/student.service';
import { AuthService } from '../../auth/auth.service';
import { CourseService } from 'src/app/services/course.service';
import { Router, NavigationEnd } from '@angular/router';


@Component({
  selector: 'app-students-cont',
  templateUrl: './students-cont.component.html',
  styleUrls: ['./students-cont.component.css']
})
export class StudentsContComponent implements OnInit {

  @Output() STUDENTS_ENROLLED: Student[] = []
  @Output() ALL_STUDENTS: Student[] = []

                              
  constructor(private studentService: StudentService,
              private courseService: CourseService, 
              private router: Router) { 
    
  }

  ngOnInit(): void {
    this.studentService.all().subscribe(
      (data) => {
        this.ALL_STUDENTS = data;
      },
      (error) => { 
        console.log("studenti non reperiti");
       } 
      );

      this.loadStudentsEnrolled();

      this.router.events.subscribe((event) => {
        if (event instanceof NavigationEnd) { 
          if (event.urlAfterRedirects.match("/students")) {
            this.loadStudentsEnrolled();
          }
        }
      });
  }

  loadStudentsEnrolled() {
    this.courseService.enrolledStudents(this.courseService.currentCourse.getValue().name).subscribe(
      (data) => {
        this.STUDENTS_ENROLLED = data;
      },
      (error) => { 
        console.log("studenti iscritti non reperiti");
       } 
      );
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

  enrollStudentCSV(file: File) {
    this.courseService.enrollStudents(this.courseService.currentCourse.getValue().name, file).subscribe(
      (data) => {
        data.forEach(Student => {
          this.STUDENTS_ENROLLED.push(Student);
        });
      },
      (error) => {
        console.log("Studenti non aggiunti");
      }
    )
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

}
