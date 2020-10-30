import { Component, EventEmitter, OnInit, Output, Inject, AfterViewInit, OnDestroy } from '@angular/core';
import { Student } from '../../models/student.model';
import { StudentService } from '../../services/student.service';
import { AuthService } from '../../auth/auth.service';
import { CourseService } from 'src/app/services/course.service';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';
import { StudentGroup } from 'src/app/models/student-group.model';


@Component({
  selector: 'app-students-cont',
  templateUrl: './students-cont.component.html',
  styleUrls: ['./students-cont.component.css']
})
export class StudentsContComponent implements OnInit, OnDestroy {

  @Output() STUDENTS_ENROLLED: StudentGroup[] = []
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
          const status: number = error.error.status;

          if (status == 404 || status == 403) {
            this.router.navigateByUrl("home");
            this.courseService.courseReload.emit();
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
    this.courseService.getEnrolledStudentsAllInfo(courseName).subscribe(
      (data) => {
        let array: StudentGroup[] = new Array();

        data.forEach(s => {
          let student: Student = s.student;
          let teamName: string = s.teamName;
          array.push(new StudentGroup(student.id, student.firstName, student.name, student.email, teamName));
        });

        this.STUDENTS_ENROLLED = array;
      },
      (error) => { 
        window.alert(error.error.message);
        const status: number = error.error.status;

        if (status == 404 || status == 403) {
          this.router.navigateByUrl("home");
          this.courseService.courseReload.emit();
        }
      } 
    );
  }

  enrollStudent(student: Student) {
    this.courseService.enrollOne(this.courseService.currentCourse.getValue().name, student.id).subscribe(
      (data) => {
        let s: StudentGroup = new StudentGroup(student.id, student.firstName, student.name, student.email, "/");
        this.STUDENTS_ENROLLED = this.STUDENTS_ENROLLED.concat(s);
      },
      (error) => { 
        window.alert(error.error.message);
        const status: number = error.error.status;

        if (status == 404 || status == 403) {
          this.router.navigateByUrl("home");
          this.courseService.courseReload.emit();
        }
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
          let s: StudentGroup = new StudentGroup(student.id, student.firstName, student.name, student.email, "/");
          this.STUDENTS_ENROLLED.push(s);
        });
      },
      (error) => {
        window.alert(error.error.message);
        const status: number = error.error.status;

        if (status == 404 || status == 403) {
          this.router.navigateByUrl("home");
          this.courseService.courseReload.emit();
        }
      }
    )
  }

  removeStudents(students: Student[]) {
    this.courseService.deleteStudentsFromCourse(this.courseService.currentCourse.getValue().name, students.map(student => student.id)).subscribe(
      (data) => {
        let array: StudentGroup[] = this.STUDENTS_ENROLLED;
        this.STUDENTS_ENROLLED = new Array();

        data.forEach(student => {
          let remove: StudentGroup = null;

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
        window.alert(error.error.message);
        const status: number = error.error.status;

        if (status == 404 || status == 403) {
          this.router.navigateByUrl("home");
          this.courseService.courseReload.emit();
        }
      }
    );
  }

}
