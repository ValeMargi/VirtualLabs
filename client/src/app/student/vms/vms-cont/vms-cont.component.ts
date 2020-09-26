import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { VM } from '../../../models/vm.model';
import { AuthService } from '../../../auth/auth.service';
import { TeamService } from '../../../services/team.service';
import { Team } from '../../../models/team.model';
import { CourseService } from 'src/app/services/course.service';
import { Student } from 'src/app/models/student.model';
import { StudentService } from 'src/app/services/student.service';

@Component({
  selector: 'app-vms-cont',
  templateUrl: './vms-cont.component.html',
  styleUrls: ['./vms-cont.component.css']
})
export class VmsContComponent implements OnInit {

  public VMs: VM[] = []
  public TEAM: Team;

  constructor(private teamService: TeamService, 
    private courseService: CourseService, 
    private studentService: StudentService) { 
    
  }


  ngOnInit() {
    let courseName = this.courseService.currentCourse.getValue().name;

    this.teamService.getTeamForStudent(courseName, this.studentService.currentStudent.id).subscribe(
      (data) => {
        if (data != null) {
          this.TEAM = data;
          
          this.teamService.getAllVMTeam(courseName, this.TEAM.id).subscribe(
            (data) => {
              this.VMs = data;
            },
            (error) => {
              console.log("Impossibile reperire le VM per il team")
            }
          );
        }
      },
      (error) => {
        console.log("Impossibile reperire il team dello studente")
      }
    );

    this.studentService.vmCreation.subscribe(
      (data) => {
        if (this.VMs.length == 0) {
          let array: VM[] = new Array();
          array.push(data);
          this.VMs = array;
        }
        else {
          this.VMs.push(data);
        }
      }, 
      (error) => {

      }
    );
  }

}
