import { CourseService } from './../../../services/course.service';
import { TeamService } from './../../../services/team.service';
import { StudentService } from './../../../services/student.service';
import { Component, Input, OnInit, Output } from '@angular/core';
import { Team } from '../../../models/team.model';
import { Student } from 'src/app/models/student.model';
import { Course } from 'src/app/models/course.model';
@Component({
  selector: 'app-teams-cont',
  templateUrl: '../teams-cont/teams-cont.component.html',
  styleUrls: ['../teams-cont/teams-cont.component.css']
})
export class TeamsContComponent implements OnInit {

  constructor(private studentService: StudentService,
              private teamService: TeamService,
              private courseService: CourseService ) { }

  @Output() public TEAMS: Team[] = [];


  @Output() public StudentInTeam: Student[] = [];

  public Members: Student[] = [];

  public team: Team;

  public proposal: any[];

  ngOnInit(): void {

      this.teamService.getStudentsInTeams(this.courseService.currentCourse.getValue().name).subscribe(
        (data) => {
          this.StudentInTeam = data;
        },
        (error) => {
          console.log("Studenti del team non caricato");
        }
      );

      this.studentInTeam();

  }

  studentInTeam(){
    this.StudentInTeam.forEach(element => {
        if(element.id == this.studentService.currentStudent.id){
          this.teamService.getMembersTeam(this.teamService.currentTeam.id).subscribe(
            (data) => {
              this.Members = data;
            },
            (error) => {
              console.log("Membri non caricato");
            }
          );
        }
        else{

          this.teamService.getProposals(this.courseService.currentCourse.getValue().name)
          .subscribe(
            (data) => {
              this.proposal = data;
            },
            (error) => {
              console.log("Errore nel caricare le proposte per lo studente");
            }
          );


        }
    });

  }

}
