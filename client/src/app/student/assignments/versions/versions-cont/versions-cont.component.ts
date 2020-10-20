import { Component, OnInit, Output, Input } from '@angular/core';
import { Student } from 'src/app/models/student.model';
import { Homework } from 'src/app/models/homework.model'
import { HomeworkVersion } from 'src/app/models/homework-version.model';
import { HomeworkCorrection } from 'src/app/models/homework-correction.model';
import { StudentService } from 'src/app/services/student.service';
import { CourseService } from 'src/app/services/course.service';
import { ActivatedRoute } from '@angular/router';
import { Assignment } from 'src/app/models/assignment.model';

@Component({
  selector: 'app-versions-cont',
  templateUrl: './versions-cont.component.html',
  styleUrls: ['./versions-cont.component.css']
})
export class VersionsContComponent implements OnInit {

  @Input() public assignment: Assignment;
  @Output() public VERSIONS: HomeworkVersion[] = [];
  @Output() public CORRECTIONS: HomeworkCorrection[] = [];


  @Output() public hw: Homework;

  constructor(private studentService: StudentService,
              private courseService: CourseService,
              private route: ActivatedRoute) { }

  ngOnInit(): void {
    //this.route.params.subscribe( params => console.log(params));

    this.studentService.getHomework(this.courseService.currentCourse.getValue().name, this.assignment.id).subscribe(
      (data) => {
        this.hw = data;
      },
      (error) => {
        console.log("Impossibile ottenere le versioni");
      }
    );


    this.studentService.getVersionsHMForStudent(this.courseService.currentCourse.getValue().name, this.assignment.id).subscribe(
      (data) => {
        this.VERSIONS = data;
      },
      (error) => {
        console.log("Impossibile ottenere le versioni");
      }
    );

    this.studentService.getCorrectionsHMForStudent(this.courseService.currentCourse.getValue().name, this.assignment.id).subscribe(
      (data) => {
        this.CORRECTIONS = data;
      },
      (error) => {
        console.log("Impossibile ottenere le revisioni");
      }
    );
  }

}
