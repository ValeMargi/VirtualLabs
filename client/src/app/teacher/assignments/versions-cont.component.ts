import { Component, OnInit, Output, Input } from '@angular/core';
import { HomeworkVersion } from 'src/app/models/homework-version.model';
import { Student } from 'src/app/models/student.model';
import { Homework } from 'src/app/models/homework.model';
import { TeacherService } from 'src/app/services/teacher.service';
import { CourseService } from 'src/app/services/course.service';
import { HomeworkCorrection } from 'src/app/models/homework-correction.model';

@Component({
  selector: 'app-versions-cont',
  templateUrl: './versions-cont.component.html',
  styleUrls: ['./versions-cont.component.css']
})
export class VersionsContComponent implements OnInit {

  @Input() public homework: Homework;
  @Output() public VERSIONS: HomeworkVersion[] = [];
  @Output() public CORRECTIONS: HomeworkCorrection[] = [];

  constructor(private teacherService: TeacherService,
              private courseService: CourseService) { }

  ngOnInit(): void {
    this.teacherService.getVersionsHMForProfessor(this.courseService.currentCourse.getValue().name, -1, this.homework.id).subscribe(
      (data) => {
        this.VERSIONS = data;
      },
      (error) => {
        console.log("Impossibile ottenere le versioni");
      }                                          
    );

    this.teacherService.getCorrectionsHMForProfessor(this.courseService.currentCourse.getValue().name, -1, this.homework.id).subscribe(
      (data) => {
        this.CORRECTIONS = data;
      },
      (error) => {
        console.log("Impossibile ottenere le revisioni");
      }
    );
  }

}
