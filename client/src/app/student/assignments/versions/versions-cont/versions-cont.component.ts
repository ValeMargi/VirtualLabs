import { Component, OnInit, Output, Input, OnDestroy, OnChanges, SimpleChanges } from '@angular/core';
import { Student } from 'src/app/models/student.model';
import { Homework } from 'src/app/models/homework.model'
import { HomeworkVersion } from 'src/app/models/homework-version.model';
import { HomeworkCorrection } from 'src/app/models/homework-correction.model';
import { StudentService } from 'src/app/services/student.service';
import { CourseService } from 'src/app/services/course.service';
import { ActivatedRoute } from '@angular/router';
import { Assignment } from 'src/app/models/assignment.model';
import { Subscription } from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-versions-cont-student',
  templateUrl: './versions-cont.component.html',
  styleUrls: ['./versions-cont.component.css']
})
export class VersionsContComponent implements OnInit, OnChanges, OnDestroy {
  @Output() HOMEWORK: Homework;
  @Output() VERSIONS: HomeworkVersion[] = [];
  @Output() CORRECTIONS: HomeworkCorrection[] = [];

  private route$: Subscription;
  private id: number;

  constructor(private studentService: StudentService,
              private courseService: CourseService,
              private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route$ = this.route.params.subscribe(params => {
      this.id = params.idA;

      if (this.id == null) {
        return;
      }

      if (history.state != null) {
        this.HOMEWORK = history.state.homework;
      }

      let courseName: string = this.courseService.currentCourse.getValue().name;

      this.studentService.getVersionsHMForStudent(courseName, this.id).subscribe(
        (data) => {
          this.VERSIONS = data;
        },
        (error) => {
          window.alert("Impossibile ottenere le versioni");
        }
      );

      this.studentService.getCorrectionsHMForStudent(courseName, this.id).subscribe(
        (data) => {
          this.CORRECTIONS = data;
        },
        (error) => {
          window.alert("Impossibile ottenere le revisioni");
        }
      );
    });

    this.studentService.verUpload.subscribe(
      (data) => {
        let array: HomeworkVersion[] = this.VERSIONS;
        this.VERSIONS = new Array();
        array.push(data);

        array.forEach(ass => {
          this.VERSIONS.push(ass);
        });
      },
      (error) => {

      }
    );
  }

  ngOnChanges(changes: SimpleChanges) {
    
  }

  ngOnDestroy() {
    this.route$.unsubscribe();
  }
}
