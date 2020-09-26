import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { VM } from 'src/app/models/vm.model';
import { CourseService } from 'src/app/services/course.service';
import { StudentService } from 'src/app/services/student.service';
import { CreateVmsComponent } from '../create-vms.component';

@Component({
  selector: 'app-create-vms-cont',
  templateUrl: './create-vms-cont.component.html',
  styleUrls: ['./create-vms-cont.component.css']
})
export class CreateVmsContComponent implements OnInit {

  constructor(private matDialogRef: MatDialogRef<CreateVmsComponent>, 
    private studentService: StudentService,
    private courseService: CourseService) { }

  ngOnInit(): void {
  }

  createVM(content: any) {
    let file: File = content.file;
    let vm: VM = content.vm;
    console.log(vm);

    this.studentService.addVM(this.courseService.currentCourse.getValue().name, file, vm).subscribe(
      (data) => {
        this.matDialogRef.close();
        this.studentService.vmCreation.emit(data);
      }, 
      (error) => {
        console.log("Errore nella creazione della VM");
      }
    );
  }

}
