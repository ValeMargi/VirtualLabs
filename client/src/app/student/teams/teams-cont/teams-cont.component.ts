import { CourseService } from './../../../services/course.service';
import { TeamService } from './../../../services/team.service';
import { StudentService } from './../../../services/student.service';
import { Component, Input, OnInit, Output } from '@angular/core';
import { Team } from '../../../models/team.model';
import { Student } from 'src/app/models/student.model';
import { Course } from 'src/app/models/course.model';
import { Proposal } from 'src/app/models/proposal.model';
@Component({
  selector: 'app-teams-cont',
  templateUrl: '../teams-cont/teams-cont.component.html',
  styleUrls: ['../teams-cont/teams-cont.component.css']
})
export class TeamsContComponent implements OnInit {

  constructor(private studentService: StudentService,
              private teamService: TeamService,
              private courseService: CourseService ) { }

  @Output() TEAM: Team;
  @Output() PROPOSALS: Proposal[];
  @Output() StudentInTeam: Student[] = [];

  public Members: Student[] = [];

  ngOnInit(): void {
    
      this.teamService.getStudentsInTeams(this.courseService.currentCourse.getValue().name).subscribe(
        (data) => {
          //this.StudentInTeam = data;
          this.studentInTeam(data);
        },
        (error) => {
          console.log("Studenti del team non caricato");
        }
      );

  }

  studentInTeam(students: Student[]) {
    if (students.length > 0) {
      students.forEach(element => {
          if (element.id == this.studentService.currentStudent.id) {
            this.teamService.getMembersTeam(this.teamService.currentTeam.id).subscribe(
              (data) => {
                this.Members = data;
                console.log(data)
              },
              (error) => {
                console.log("Membri non caricato");
              }
            );
          }
          else{
            this.getProposals();
          }
      });
    }
    else {
      this.getProposals();
    }

  }

  getProposals() {
    this.teamService.getProposals(this.courseService.currentCourse.getValue().name).subscribe(
      (data) => {
        this.PROPOSALS = data;
        console.log(data)
      },
      (error) => {
        window.alert("Errore nel caricare le proposte per lo studente");
      }
    );
  }

  acceptProposal(token: string) {
    this.teamService.confirm(token);
  }

  refuseProposal(token: string) {
    this.teamService.refuse(token);
  }

}
