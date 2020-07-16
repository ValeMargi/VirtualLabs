import { Injectable } from '@angular/core';
import { Student } from '../student.model';
import { of, Observable, from } from 'rxjs';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { map, switchMap, concatMap, toArray } from 'rxjs/operators';
import { AuthService } from '../auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  constructor(private http: HttpClient, private authService: AuthService) {}

  MY_PAHT = "http://localhost:3000/students";

  create(student: Student) {
    return this.http.post<Student>(this.MY_PAHT, student);
  }

  update(student: Student) {
    return this.http.put<Student>(this.MY_PAHT, student);
  }

  find(id: string): Observable<Student> {
    return this.http.get<Student>(`${this.MY_PAHT}/${id}`);
  }

  query(): Observable<Student[]> {
    return this.http.get<Student[]>(`${this.MY_PAHT}`)
                    .pipe(map(students => students || []));
  }

  delete(id: string) {
    return this.http.delete(`${this.MY_PAHT}/${id}`);
  }

  enroll(students: Student[], courseId: number) {
    return from(students).pipe(
      concatMap(student => {
        student.courseId = courseId;

        return this.http.put<Student>(`${this.MY_PAHT}/${student.id}`, student);
      }),
      toArray()
    );
  }

  unenroll(students: Student[]) {
    return from(students).pipe(
      concatMap(student => {
        student.courseId = 0;

        return this.http.put<Student>(`${this.MY_PAHT}/${student.id}`, student);
      }),
      toArray()
    );
  }

  enrolledStudents(courseId): Observable<Student[]> {
    return this.http.get<Student[]>(`${this.MY_PAHT}?courseId=${courseId}`)
                    .pipe(map(students => students || []));
  }
}
