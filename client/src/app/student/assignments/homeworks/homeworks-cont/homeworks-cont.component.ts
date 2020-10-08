import { Component, OnInit, Output, Input, OnDestroy } from '@angular/core';
import { Homework } from 'src/app/models/homework.model';
import { Assignment } from 'src/app/models/assignment.model';
import { StudentService } from 'src/app/services/student.service';
import { CourseService } from 'src/app/services/course.service';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-homeworks-cont',
  templateUrl: './homeworks-cont.component.html',
  styleUrls: ['./homeworks-cont.component.scss']
})
export class HomeworksContComponent implements OnInit, OnDestroy {

  private route$: Subscription;

  @Input() public assignment: Assignment;
  @Output() public HOMEWORKS: Homework[] = [];

  constructor(private studentService: StudentService,
              private courseService: CourseService,
              private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route$ = this.route.params.subscribe(params => {
      this.studentService.allHomework(this.courseService.currentCourse.getValue().name, params.idH).subscribe(
        (data) => {
          console.log(data)
          this.HOMEWORKS = data;
        },
        (error) => {
          console.log("Impossibile ottenere gli homeworks");
        }
      );
    });
  }

  ngOnDestroy() {
    this.route$.unsubscribe();
  }

}
