import { Component, EventEmitter, OnInit, Output, Inject, AfterViewInit, OnDestroy } from '@angular/core';
import { Student } from '../../models/student.model';
import { StudentService } from '../../services/student.service';
import { AuthService } from '../../auth/auth.service';
import { CourseService } from 'src/app/services/course.service';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';


@Component({
  selector: 'app-students-cont',
  templateUrl: './students-cont.component.html',
  styleUrls: ['./students-cont.component.css']
})
export class StudentsContComponent implements OnInit, OnDestroy {

  @Output() STUDENTS_ENROLLED: Student[] = []
  @Output() ALL_STUDENTS: Student[] = []

  private route$: Subscription
                              
  constructor(private studentService: StudentService,
              private courseService: CourseService, 
              private router: Router,
              private route: ActivatedRoute) { 
    
  }

  ngOnInit(): void {
    this.route$ = this.route.params.subscribe(params => {
      let courseName = params.courses;

      if (courseName == undefined) {
        return;
      }

      this.studentService.all().subscribe(
        (data) => {
          this.ALL_STUDENTS = data;
        },
        (error) => { 
          window.alert(error.error.message);

          if (error.error.status == 404) {
            this.router.navigateByUrl("home");
          }
        } 
      );

      this.loadStudentsEnrolled(courseName);
    });
  }

  ngOnDestroy() {
    this.route$.unsubscribe();
  }

  loadStudentsEnrolled(courseName: string) {
    this.courseService.enrolledStudents(courseName).subscribe(
      (data) => {
        this.STUDENTS_ENROLLED = data;
      },
      (error) => { 
        window.alert(error.error.message);
        const status: number = error.error.status;

        if (status == 404 || status == 403) {
          this.router.navigateByUrl("home");
        }
      } 
    );
  }

  enrollStudent(student: Student) {
    this.courseService.enrollOne(this.courseService.currentCourse.getValue().name, student.id).subscribe(
      (data) => {
        this.STUDENTS_ENROLLED = this.STUDENTS_ENROLLED.concat(student);
      },
      (error) => { 
        window.alert("studente non aggiunto");
      } 
      );
  }

  enrollStudentCSV(file: File) {
    this.courseService.enrollStudents(this.courseService.currentCourse.getValue().name, file).subscribe(
      (data) => {
        let array: Student[] = this.STUDENTS_ENROLLED;
        this.STUDENTS_ENROLLED = new Array();
        data.forEach(s => array.push(s));

        array.forEach(student => {
          this.STUDENTS_ENROLLED.push(student);
        });
      },
      (error) => {
        window.alert("Studenti non aggiunti");
      }
    )
  }

  removeStudents(students: Student[]) {
    this.courseService.deleteStudentsFromCourse(this.courseService.currentCourse.getValue().name, students.map(student => student.id)).subscribe(
      (data) => {
        let array: Student[] = this.STUDENTS_ENROLLED;
        this.STUDENTS_ENROLLED = new Array();

        data.forEach(student => {
          let remove: Student = null;

          array.forEach(s => {
            if (s.id == student.id) {
              remove = s;
            }
          });

          if (remove != null) {
            array.splice(array.indexOf(remove), 1);
          }
        });

        array.forEach(s => this.STUDENTS_ENROLLED.push(s));
      },
      (error) => { 
        window.alert("Rimozione studenti non avvenuta");
       }
    );
  }

}
