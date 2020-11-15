import { Team } from './../../../../models/team.model';
import { TeamService } from 'src/app/services/team.service';
import { CourseService } from './../../../../services/course.service';
import { Component, OnInit, Output } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { Student } from 'src/app/models/student.model';
import { StudentService } from 'src/app/services/student.service';
import { Course } from 'src/app/models/course.model';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';

@Component({
  selector: 'app-request-team-dialog-cont',
  templateUrl: './request-team-dialog-cont.component.html',
  styleUrls: ['./request-team-dialog-cont.component.css']
})
export class RequestTeamDialogContComponent implements OnInit {

  QUERYING: boolean = false;
  COURSE: Course;
  AVAILABLE_STUDENTS: Student[] = [];

  constructor(private courseService: CourseService,
              private teamService: TeamService,
              private location: Location) { }

  ngOnInit(): void {
    const course: Course = this.courseService.currentCourse.getValue();

    if (course.max == -1 || course.min == -1) {
      //se non ci sono le informazioni, si vanno a reperire...
      this.courseService.getOne(course.name).subscribe(
        (data) => {
          this.COURSE = data;
          this.courseService.currentCourse.next(data);  //... e si aggiorna l'oggetto nel CourseService
          this.getStudents(this.COURSE.name);
        },
        (error) => {
          window.alert(error.error.message);
        }
      );
    }
    else {
      this.COURSE = course;
      this.getStudents(this.COURSE.name);
    }
    
  }

  getStudents(courseName: string) {
    //reperisco gli studenti disponibili a formare il team
    this.teamService.getAvailableStudents(courseName).subscribe(
      (data) => {
        this.AVAILABLE_STUDENTS = data;
      },
      (error) => {
        window.alert(error.error.message);
      }
    )
  }

  proposeTeam(content: any) {
    this.QUERYING = true;
    let teamName: string = content.teamName;
    let timeout: string = content.timeout;
    let membersId: string[] = content.membersId;

    this.teamService.proposeTeam(this.courseService.currentCourse.getValue().name, teamName, timeout, membersId).subscribe(
      (data) => {
        //se va a buon fine, si torna alla schermata delle proposte e si notifica la creazione della proposta
        this.QUERYING = false;
        this.location.back();
        this.teamService.proposal.emit(data);
      },
      (error) =>{
        window.alert(error.error.message);
        this.QUERYING = false;
      }
    );
  }


}
