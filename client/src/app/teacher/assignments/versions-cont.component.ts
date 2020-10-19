import { Component, OnInit, Output, Input, OnDestroy } from '@angular/core';
import { HomeworkVersion } from 'src/app/models/homework-version.model';
import { Student } from 'src/app/models/student.model';
import { Homework } from 'src/app/models/homework.model';
import { TeacherService } from 'src/app/services/teacher.service';
import { CourseService } from 'src/app/services/course.service';
import { HomeworkCorrection } from 'src/app/models/homework-correction.model';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-versions-cont-teacher',
  templateUrl: './versions-cont.component.html',
  styleUrls: ['./versions-cont.component.css']
})
export class VersionsContComponent implements OnInit, OnDestroy {

  @Input() homework: Homework;
  @Output() HOMEWORK: Homework;
  @Output() VERSIONS: HomeworkVersion[] = [];
  @Output() CORRECTIONS: HomeworkCorrection[] = [];

  private route$: Subscription;
  private idA: number;

  constructor(private teacherService: TeacherService,
              private courseService: CourseService,
              private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route$ = this.route.params.subscribe(params => {
      this.idA = params.idA;
      console.log(this.idA)

      if (this.idA == null) {
        return;
      }

      this.teacherService.getVersionsHMForProfessor(this.courseService.currentCourse.getValue().name, this.idA, this.homework.id).subscribe(
        (data) => {
          this.VERSIONS = data;
        },
        (error) => {
          console.log("Errore nel reperire le versioni");
        }                                          
      );

      this.teacherService.getCorrectionsHMForProfessor(this.courseService.currentCourse.getValue().name, this.idA, this.homework.id).subscribe(
        (data) => {
          this.CORRECTIONS = data;
        },
        (error) => {
          window.alert("Errore nel reperire le revisioni");
        }
      );
    });
  }

  ngOnDestroy() {
    this.route$.unsubscribe();
  }

  uploadCorrection(file: File) {
    //this.teacherService.uploadCorrection(this.courseService.currentCourse.getValue().name, this.idA, this.homework.id, -1, file, )
  }

}
