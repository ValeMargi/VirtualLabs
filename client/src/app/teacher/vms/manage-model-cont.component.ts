import { Component, OnInit, Output } from '@angular/core';
import { CourseService } from 'src/app/services/course.service';
import { Course } from 'src/app/models/course.model';
import { TeacherService } from 'src/app/services/teacher.service';

@Component({
  selector: 'app-manage-model-cont',
  templateUrl: './manage-model-cont.component.html',
  styleUrls: ['./manage-model-cont.component.css']
})
export class ManageModelContComponent implements OnInit {

  @Output() MODEL_VM: Course;

  constructor(private courseService: CourseService, private teacherService: TeacherService) { }

  ngOnInit(): void {
    this.MODEL_VM = this.courseService.currentCourse.getValue();
  }

  updateModel(modelvm: Course) {
    this.teacherService.updateModelVM(modelvm.name, modelvm).subscribe(
      (data) => {
        this.courseService.currentCourse.next(modelvm);
      },
      (error) => {
        console.log("modello non aggiornato");
      }
    )
  }

}
