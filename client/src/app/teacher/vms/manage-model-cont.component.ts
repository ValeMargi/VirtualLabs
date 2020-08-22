import { Component, OnInit, Output } from '@angular/core';
import { CourseService } from 'src/app/services/course.service';
import { Course } from 'src/app/models/course.model';

@Component({
  selector: 'app-manage-model-cont',
  templateUrl: './manage-model-cont.component.html',
  styleUrls: ['./manage-model-cont.component.css']
})
export class ManageModelContComponent implements OnInit {

  @Output() MODEL_VM: Course;

  constructor(private courseService: CourseService) { }

  ngOnInit(): void {
    this.MODEL_VM = this.courseService.currentCourse.getValue();
    //this.MODEL_VM = new Course("Applicazioni Internet", "AI", 10, 250, false, 4, 100, 8, 5, 5);

  }

}
