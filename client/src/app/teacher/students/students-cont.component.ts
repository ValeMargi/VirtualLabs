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

/*STUDENTS_ENROLLED: Student[] = [new Student('s267782', 'Mosconi', 'Germano', 0, 0),
                                  new Student('s268882', 'Brazorf', 'Ajeje', 0, 0),
                                  new Student('s269982', 'Esposito', 'Mohamed', 0, 0),
                                  new Student('s265482', 'La Barca', 'Remo', 0, 0),
                                  new Student('s265582', 'Saolini', 'Gianmarco', 0, 0)];

ALL_STUDENTS: Student[] = [new Student('s218582', 'Baglio', 'Aldo', 0, 0),
                                  new Student('s268877', 'Di Lernia', 'Leone', 0, 0),
                                  new Student('s211983', 'Smaila', 'Umberto', 0, 0),
                                  new Student('s263280', 'Stilton', 'Geronimo', 0, 0),
                                  new Student('s260001', 'Zalone', 'Checco', 0, 0),
                                  new Student('s267782', 'Mosconi', 'Germano', 0, 0),
                                  new Student('s268882', 'Brazorf', 'Ajeje', 0, 0),
                                  new Student('s269982', 'Esposito', 'Mohamed', 0, 0),
                                  new Student('s265482', 'La Barca', 'Remo', 0, 0),
                                  new Student('s265582', 'Saolini', 'Gianmarco', 0, 0)];*/

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
      (error) => {  } 
      );

    this.courseService.enrolledStudents(this.courseService.currentCourse.name).subscribe(
      (data) => {
        console.log(data);
        this.STUDENTS_ENROLLED = data;
        this.enrolledStudents.emit(this.STUDENTS_ENROLLED);
      },
      (error) => {  } 
      );
    
  }

  ngOnInit(): void {
  
  }

  enrollStudent(student: Student) {
    this.courseService.enrollOne(this.courseService.currentCourse.name, student.id).subscribe(
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
    /*this.courseService.(students).subscribe(
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
      (error) => {  }
    );*/
  }

}
