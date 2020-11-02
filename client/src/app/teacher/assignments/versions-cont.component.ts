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

  HOMEWORK: Homework;
  VERSIONS: HomeworkVersion[] = [];
  CORRECTIONS: HomeworkCorrection[] = [];

  private route$: Subscription;

  constructor(private teacherService: TeacherService,
              private courseService: CourseService,
              private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route$ = this.route.parent.params.subscribe(params => {
      const idA: number = +params.idA;
      const courseName: string = this.courseService.currentCourse.getValue().name;
      const idHw: number = +this.route.snapshot.params.idH;

      if (idA == null || courseName == null || idHw == null) {
        return;
      }

      if (history.state != null) {
        this.HOMEWORK = history.state.homework;
      }

      if (this.HOMEWORK == null) {
        this.teacherService.allHomework(courseName, idA).subscribe(
          (data) => {
            data.forEach(element => {
              let homework: Homework = element.Homework;
              
              if (homework.id == idHw) {
                this.HOMEWORK = homework;
              }
            });
          },
          (error) => {
            window.alert(error.error.message);
          }
        )
      }

      this.teacherService.getVersionsHMForProfessor(courseName, idA, idHw).subscribe(
        (data) => {
          this.VERSIONS = data;
        },
        (error) => {
          window.alert(error.error.message);
        }                                          
      );

      this.teacherService.getCorrectionsHMForProfessor(courseName, idA, idHw).subscribe(
        (data) => {
          this.CORRECTIONS = data;
        },
        (error) => {
          window.alert(error.error.message);
        }
      );
    });

    this.teacherService.corrUpload.subscribe(
      (data) => {
        let corr: HomeworkCorrection = data.corr;
        let permanent: boolean = data.permanent;

        if (permanent) {
          this.HOMEWORK.permanent = permanent;
        }

        let array: HomeworkCorrection[] = this.CORRECTIONS;
        this.CORRECTIONS = new Array();
        array.push(corr);

        array.forEach(cor => {
          this.CORRECTIONS.push(cor);
        });
      }, 
      (error) => {

      }
    );
  }

  ngOnDestroy() {
    this.route$.unsubscribe();
  }

}
