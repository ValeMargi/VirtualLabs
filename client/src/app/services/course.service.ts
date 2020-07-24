import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, concatMap, toArray } from 'rxjs/operators';
import { Course } from '../models/course.model';
import { Student } from '../models/student.model';
import { Teacher } from '../models/teacher.model';
import { Modelvm } from '../models/modelvm';

@Injectable({
  providedIn: 'root'
})
export class CourseService {

  constructor(private http: HttpClient) {}

  API_COURSES = "http://localhost:8080/courses";

  all() {
    return this.http.get<Course[]>(`${this.API_COURSES}`).pipe(map(courses => courses || []));
  }

  getOne(name: string) {
    return this.http.get<Course>(`${this.API_COURSES}/${name}`);
  }

  enrolledStudents(name: string) {
    return this.http.get<Student[]>(`${this.API_COURSES}/${name}/enrolled`).pipe(map(students => students || []));
  }

  addCourse(course: Course) {
    return this.http.post<Course>(this.API_COURSES, course);
  }

  addProfessorToCourse(name: string, teacher: Teacher) {
    return this.http.post<Teacher>(`${this.API_COURSES}/${name}/addProfessor`, teacher);
  }

  enrollOne() {
    //chiedere
  }

  enrollStudents(name: string, file: File) {
    //chiedere
    const data: FormData = new FormData();
    data.append('file', file);
    return this.http.post<File>(`${this.API_COURSES}/${name}/enrollMany`, data);
  }

  enableCourse(name: string, enabled: boolean) {
    return this.http.post<boolean>(`${this.API_COURSES}/${name}/enable`, enabled);
  }

  getCoursesForStudent(studentId: string) {
    return this.http.get<Course[]>(`${this.API_COURSES}/${studentId}`).pipe(map(courses => courses || []));
  }

  getCoursesForProfessor(teacherId: string) {
    return this.http.get<Course[]>(`${this.API_COURSES}/${teacherId}`).pipe(map(courses => courses || []));
  }

  removeCourse(courseId: string) {
    return this.http.post<void>(`${this.API_COURSES}/${courseId}/remove`, null);
  }

  modifyCourse(courseId: string, course: Course) {
    return this.http.post<Course>(`${this.API_COURSES}/${courseId}/modify`, course);
  }

  proposeTeam(courseId: string) {
    //chiarire, team controller?
    //return this.http.post<Course>(`${this.API_COURSES}/${courseId}/proposeTeam`, course);
  }

  getMembersTeam() {
    //chiarire, possibile errore nel server, manca teamId nel path
  }

  addModelVM(courseId: string, model: Modelvm) {
    return this.http.post<Modelvm>(`${this.API_COURSES}/${courseId}/addModel`, model);
  }

  addOwner(courseId: string, vmId: string, input) {
    //chiedere
    return this.http.post<void>(`${this.API_COURSES}/${courseId}/${vmId}/addOwner`, input);
  }

  //chiarire metodi VM

  activateVM() {

  }

  disableVM() {

  }

  removeVM() {

  }

  //da completare

}
