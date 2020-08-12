import { Injectable } from '@angular/core';
import { Team } from '../models/team.model';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../auth/auth.service';
import { map } from 'rxjs/operators';
import { Student } from '../models/student.model';

@Injectable({
  providedIn: 'root'
})
export class TeamService {

  constructor(private http: HttpClient) {}

  API_TEAMS = "http://localhost:8080/teams";

  proposeTeam(courseName: string, teamMap: Map<string, string[]>) {
    //nella mappa le chiavi sono nameTeam e membersId
    return this.http.post(`${this.API_TEAMS}/${courseName}/proposeTeam`, teamMap);
  }

  getMembersTeam(teamId: number) {
    return this.http.get<Student[]>(`${this.API_TEAMS}/${teamId}/members`).pipe(map(students => students || []));
  }

  getTeamsForStudent(studentId: string) {
    return this.http.get<Team[]>(`${this.API_TEAMS}/${studentId}/teams`).pipe(map(teams => teams || []));
  }

  getStudentsInTeams(courseName: string) {
    return this.http.get<Student[]>(`${this.API_TEAMS}/${courseName}/inTeam`).pipe(map(students => students || []));
  }

  getAvailableStudents(courseName: string) {
    return this.http.get<Student[]>(`${this.API_TEAMS}/${courseName}/notInTeam`).pipe(map(students => students || []));
  }

}
