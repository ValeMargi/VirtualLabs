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

  MY_PAHT = "http://localhost:3000/teams";

  create(team: Team) {
    return this.http.post<Team>(this.MY_PAHT, team);
  }

  update(team: Team) {
    return this.http.put<Team>(this.MY_PAHT, team);
  }

  find(id: string) {
    return this.http.get<Team>(`${this.MY_PAHT}/${id}`);
  }

  query() {
    return this.http.get<Team[]>(`${this.MY_PAHT}`)
                    .pipe(map(teams => teams || []));
  }

  delete(id: string) {
    return this.http.delete(`${this.MY_PAHT}/${id}`);
  }

  addStudents(team: Team, students: Student[]) {
    students.forEach(student => {
      //team.members.push(student);
    });

    return this.http.put<Team>(`${this.MY_PAHT}/${team.id}`, team);
  }

  removeStudents(team: Team, students: Student[]) {
    students.forEach(student => {
      //team.members.splice(team.members.indexOf(student), 1);
    });

    return this.http.put<Team>(`${this.MY_PAHT}/${team.id}`, team);
  }
}
