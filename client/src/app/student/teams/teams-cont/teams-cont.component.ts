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
  @Output() public REQUEST: Request[] = [];

  @Output() public StudentInTeam: Student[] = [];

  public Members: Student[] = [];

  public team: Team;

  ngOnInit(): void {

      this.teamService.getStudentsInTeams(this.courseService.currentCourse.getValue().name).subscribe(
        (data) => {
          this.StudentInTeam = data;
        },
        (error) => {
          console.log("Studenti del team non caricato");
        }
      );

      this.teamService.getMembersTeam(this.teamService.currentTeam.id).subscribe(
        (data) => {
          this.Members = data;
        },
        (error) => {
          console.log("Membri non caricato");
        }
      );
  }

  studentInTeam(){
    this.StudentInTeam.forEach(element => {
        if(element.id == this.studentService.currentStudent.id){
          this.teamService.getTeamForStudent(this.courseService.currentCourse.getValue().name, this.studentService.currentStudent.id)
          .subscribe(
            (data) => {
              this.team = data;
            },
            (error) => {
              console.log("Errore nel caricare il Team dello studente");
            }
          );
        }
        else{
          /*
          this.teamService.(this.courseService.currentCourse.getValue().name, this.studentService.currentStudent.id)
          .subscribe(
            (data) => {
              this.team = data;
            },
            (error) => {
              console.log("Errore nel caricare il Team dello studente");
            }
          );
          */
          /*
          //Proporre un Team
          this.teamService.proposeTeam(this.courseService.currentCourse.getValue().name,Map<this.teamService.currentTeam.name,> )
          .subscribe(
              (data) => {
                this.team = data;
              },
              (error) => {
                console.log("Errore nel caricameto delle richieste");
              }
            );
          */
        }
    });

  }

}
