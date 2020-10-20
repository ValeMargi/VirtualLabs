import { Injectable } from '@angular/core';
import { Team } from '../models/team.model';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../auth/auth.service';
import { map } from 'rxjs/operators';
import { Student } from '../models/student.model';
import { VM } from '../models/vm.model';

@Injectable({
  providedIn: 'root'
})
export class TeamService {

  constructor(private http: HttpClient) {}

  API_TEAMS = "http://localhost:8080/API/teams";
  currentTeam: Team;

  proposeTeam(courseName: string, teamMap: Map<string, string[]>) {
    //nella mappa le chiavi sono nameTeam e membersId
    return this.http.post<Team>(`${this.API_TEAMS}/${courseName}/proposeTeam`, teamMap);
  }

  getTeamsForCourse(courseName: string) {
    return this.http.get<Team[]>(`${this.API_TEAMS}/${courseName}/forCourse`).pipe(map(teams => teams || []));
  }

  getMembersTeam(teamId: number) {
    return this.http.get<Student[]>(`${this.API_TEAMS}/${teamId}/members`).pipe(map(students => students || []));
  }

  getTeamsForStudent(studentId: string) {
    return this.http.get<Team[]>(`${this.API_TEAMS}/${studentId}/teams`).pipe(map(teams => teams || []));
  }

  getTeamForStudent(courseId: string, studentId: string) {
    return this.http.get<Team>(`${this.API_TEAMS}/${courseId}/${studentId}/team`);
  }

  getStudentsInTeams(courseName: string) {
    return this.http.get<Student[]>(`${this.API_TEAMS}/${courseName}/inTeam`).pipe(map(students => students || []));
  }

  getAvailableStudents(courseName: string) {
    return this.http.get<Student[]>(`${this.API_TEAMS}/${courseName}/notInTeam`).pipe(map(students => students || []));
  }

  getAllVMTeam(courseName: string, teamId: number) {
    return this.http.get<VM[]>(`${this.API_TEAMS}/${courseName}/${teamId}/getVM`).pipe(map(vms => vms || []));
  }

}
