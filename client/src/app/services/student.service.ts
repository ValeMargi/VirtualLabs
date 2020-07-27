import { Injectable } from '@angular/core';
import { Student } from '../models/student.model';
import { of, Observable, from } from 'rxjs';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { map, switchMap, concatMap, toArray } from 'rxjs/operators';
import { AuthService } from '../auth/auth.service';
import { Course } from '../models/course.model';
import { Team } from '../models/team.model';

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  constructor(private http: HttpClient) {}

  API_STUDENTS = "http://localhost:3000/students";

  //metodi json server
  create(student: Student) {
    return this.http.post<Student>(this.API_STUDENTS, student);
  }

  update(student: Student) {
    return this.http.put<Student>(this.API_STUDENTS, student);
  }

  find(id: string): Observable<Student> {
    return this.http.get<Student>(`${this.API_STUDENTS}/${id}`);
  }

  query(): Observable<Student[]> {
    return this.http.get<Student[]>(`${this.API_STUDENTS}`)
                    .pipe(map(students => students || []));
  }

  delete(id: string) {
    return this.http.delete(`${this.API_STUDENTS}/${id}`);
  }

  enroll(students: Student[], courseId: number) {
    return from(students).pipe(
      concatMap(student => {
        //student.courseId = courseId;

        return this.http.put<Student>(`${this.API_STUDENTS}/${student.id}`, student);
      }),
      toArray()
    );
  }

  unenroll(students: Student[]) {
    return from(students).pipe(
      concatMap(student => {
        //student.courseId = 0;

        return this.http.put<Student>(`${this.API_STUDENTS}/${student.id}`, student);
      }),
      toArray()
    );
  }

  enrolledStudents(courseId): Observable<Student[]> {
    return this.http.get<Student[]>(`${this.API_STUDENTS}?courseId=${courseId}`)
                    .pipe(map(students => students || []));
  }
  //fine metodi json server

  all() {
    return this.http.get<Student[]>(`${this.API_STUDENTS}`).pipe(map(students => students || []));
  }

  getOne(id: string) {
    return this.http.get<Student>(`${this.API_STUDENTS}/${id}`);
  }

  getCourses(id: string) {
    return this.http.get<Course[]>(`${this.API_STUDENTS}/${id}/courses`).pipe(map(courses => courses || []));
  }

  getTeamsForStudent(id: string) {
    return this.http.get<Team[]>(`${this.API_STUDENTS}/${id}/teams`).pipe(map(teams => teams || []));
  }

  //discutere gli altri metodi

}
