import { Component, OnInit, Output, Input } from '@angular/core';
import { Homework } from 'src/app/models/homework.model';
import { Assignment } from 'src/app/models/assignment.model';
import { TeacherService } from 'src/app/services/teacher.service';
import { CourseService } from 'src/app/services/course.service';

@Component({
  selector: 'app-homeworks-cont',
  templateUrl: './homeworks-cont.component.html',
  styleUrls: ['./homeworks-cont.component.css']
})
export class HomeworksContComponent implements OnInit {

  @Input() public assignment: Assignment;
  @Output() public HOMEWORKS: Homework[] = [];

  constructor(private teacherService: TeacherService,
              private courseService: CourseService) { }

  ngOnInit(): void {
    this.teacherService.allHomework(this.courseService.currentCourse.getValue().name, this.assignment.id).subscribe(
      (data) => {
        this.HOMEWORKS = data;
      },
      (error) => {
        console.log("Impossibile ottenere gli homeworks");
      }
    )
  }

}
