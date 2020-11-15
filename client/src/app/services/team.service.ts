import { EventEmitter, Injectable } from '@angular/core';
import { Team } from '../models/team.model';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../auth/auth.service';
import { map } from 'rxjs/operators';
import { Student } from '../models/student.model';
import { VM } from '../models/vm.model';
import { Proposal } from '../models/proposal.model';


@Injectable({
  providedIn: 'root'
})
export class TeamService {

  constructor(private http: HttpClient) {}

  API_TEAMS = "http://localhost:8080/API/teams";

  proposal: EventEmitter<Proposal> = new EventEmitter<Proposal>();  //emitter per notificare la creazione di una proposta team

  proposeTeam(courseName: string, nameTeam: string, timeout: string, membersId: string[]) {
    return this.http.post<Proposal>(`${this.API_TEAMS}/${courseName}/proposeTeam`,
    { "nameTeam": nameTeam, "timeout": timeout, "membersId": membersId });
  }

  getProposals(courseName: string) {
    return this.http.get<Proposal[]>(`${this.API_TEAMS}/${courseName}/getProposals`).pipe(map(proposals => proposals || []));
  }

  getTeamsForCourse(courseName: string) {
    return this.http.get<Team[]>(`${this.API_TEAMS}/${courseName}/forCourse`).pipe(map(teams => teams || []));
  }

  //Get dei membri di team
  getMembersTeam(teamId: number) {
    return this.http.get<Student[]>(`${this.API_TEAMS}/${teamId}/members`).pipe(map(students => students || []));
  }

  getTeamsForStudent(studentId: string) {
    return this.http.get<Team[]>(`${this.API_TEAMS}/${studentId}/teams`).pipe(map(teams => teams || []));
  }

  getTeamForStudent(courseId: string, studentId: string) {
    return this.http.get<Team>(`${this.API_TEAMS}/${courseId}/${studentId}/team`);
  }

  //Get studenti che appartengono ad un team nel corso specificato
  getStudentsInTeams(courseName: string) {
    return this.http.get<Student[]>(`${this.API_TEAMS}/${courseName}/inTeam`).pipe(map(students => students || []));
  }

  //Studenti che non appartengono ancora ad un Team
  getAvailableStudents(courseName: string) {
    return this.http.get<Student[]>(`${this.API_TEAMS}/${courseName}/notInTeam`).pipe(map(students => students || []));
  }

  //Ritorna una array di VM per un determinato corso e team
  getAllVMTeam(courseName: string, teamId: number) {
    return this.http.get<VM[]>(`${this.API_TEAMS}/${courseName}/${teamId}/getVM`).pipe(map(vms => vms || []));
  }

  confirm(token: string) {
    //0 = non valido, 1 = conferma, 2 = creato
    return this.http.get<number>(`${this.API_TEAMS}/notification/confirm/${token}`);
  }

  refuse(token: string) {
    //0 = non valido, 1 = respinto
    return this.http.get<number>(`${this.API_TEAMS}/notification/reject/${token}`);
  }

}
