import { Injectable } from '@angular/core';
import { Course } from '../course.model';
import { HttpClient } from '@angular/common/http';
import { map, concatMap, toArray } from 'rxjs/operators';
import { Student } from '../student.model';
import { from } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CourseService {

  constructor(private http: HttpClient) {}

  MY_PAHT = "http://localhost:3000/course";

  create(course: Course) {
    return this.http.post<Course>(this.MY_PAHT, course);
  }

  update(course: Course) {
    return this.http.put<Course>(this.MY_PAHT, course);
  }

  find(id: string) {
    return this.http.get<Course>(`${this.MY_PAHT}/${id}`);
  }

  query() {
    return this.http.get<Course[]>(`${this.MY_PAHT}`)
                    .pipe(map(courses => courses || []));
  }

  delete(id: string) {
    return this.http.delete(`${this.MY_PAHT}/${id}`);
  }

  enrollStudents(students: Student[], courseId: number) {

  }

  unenrollStudents(students: Student[]) {

  }

  enrolledStudents(courseId) {
   
  }
}
