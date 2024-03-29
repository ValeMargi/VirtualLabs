import { Injectable, Output, EventEmitter } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { map, concatMap, toArray } from 'rxjs/operators';
import { Course } from '../models/course.model';
import { Student } from '../models/student.model';
import { Teacher } from '../models/teacher.model';
import { HomeworkVersion } from '../models/homework-version.model';
import { HomeworkCorrection } from '../models/homework-correction.model';
import { BehaviorSubject } from 'rxjs';
import { PhotoHomeworkVersion } from '../models/photo-homework-version.model';
import { PhotoHomeworkCorrection } from '../models/photo-homework-correction.model';

@Injectable({
  providedIn: 'root'
})
export class CourseService {

  currentCourse = new BehaviorSubject<Course>(new Course("", "", -1, -1, 0, -1, -1, -1, -1, -1)); //corso corrente
  currentCourse$ = this.currentCourse.asObservable();

  courseRemove: EventEmitter<string> = new EventEmitter<string>();  //emitter per notificare la rimozione di un corso
  courseReload: EventEmitter<void> = new EventEmitter<void>();  //emitter per richiedere all'app component il ricaricamento dei corsi 

  constructor(private http: HttpClient) {}

  API_COURSES = "http://localhost:8080/API/courses";


  setCurrentCourse(course: Course) {
    this.currentCourse.next(course);
  }

  all() {
    return this.http.get<Course[]>(`${this.API_COURSES}`).pipe(map(courses => courses || []));
  }

  getOne(name: string) {
    return this.http.get<Course>(`${this.API_COURSES}/${name}`);
  }

  addCourse(course: Course, file: File, teachersId: string[]) {
    let data: FormData = new FormData();
    data.append('course', new Blob([JSON.stringify(course)], {
    type: "application/json" }));
    data.append("file", file, file.name);
    data.append("professors", new Blob([JSON.stringify(teachersId)], {
    type: "application/json" }));

    return this.http.post<Course>(`${this.API_COURSES}/`, data);
  }

  enableCourse(name: string, enabled: boolean) {
    return this.http.post(`${this.API_COURSES}/${name}/enable`, enabled.toString());
  }

  removeCourse(courseName: string) {
    return this.http.delete<boolean>(`${this.API_COURSES}/${courseName}/remove`);
  }

  modifyCourse(courseName: string, course: Course) {
    return this.http.post<boolean>(`${this.API_COURSES}/${courseName}/modify`, course);
  }

  addProfessorsToCourse(courseName: string, teachersId: string[]) {
    return this.http.post<Teacher[]>(`${this.API_COURSES}/${courseName}/addProfessors`, teachersId);
  }

  enrollOne(courseName: string, studentId: string) {
    return this.http.post(`${this.API_COURSES}/${courseName}/enrollOne`, studentId);
  }

  enrollStudents(courseName: string, file: File) {
    let data: FormData = new FormData();
    data.append("file", file, file.name);
    return this.http.post<Student[]>(`${this.API_COURSES}/${courseName}/enrollMany`, data).pipe(map(students => students || []));
  }

  enrollAll(courseName: string, studentsIds: string[]) {
    return this.http.post<string[]>(`${this.API_COURSES}/${courseName}/enrollAll`, studentsIds);
  }

  deleteStudentsFromCourse(courseName: string, studentsId: string[]) {
    return this.http.post<Student[]>(`${this.API_COURSES}/${courseName}/removeStudents`, studentsId).pipe(map(students => students || []));
  }
  
  enrolledStudents(courseName: string) {
    return this.http.get<Student[]>(`${this.API_COURSES}/${courseName}/enrolled`).pipe(map(students => students || []));
  }

  getEnrolledStudentsAllInfo(courseName: string) {
    return this.http.get<any[]>(`${this.API_COURSES}/${courseName}/enrolledInfo`).pipe(map(students => students || []));
  }

  getProfessorsForCourse(courseName: string) {
    return this.http.get<Teacher[]>(`${this.API_COURSES}/${courseName}/getProfessors`).pipe(map(teachers => teachers || []));
  }

  getVersionHM(courseName: string, assignmentId: number, homeworkId: number, versionId: number) {
    return this.http.get<PhotoHomeworkVersion>(`${this.API_COURSES}/${courseName}/${assignmentId}/${homeworkId}/${versionId}/version`);
  }

  getCorrectionHM(courseName: string, assignmentId: number, homeworkId: number, correctionId: number) {
    return this.http.get<PhotoHomeworkCorrection>(`${this.API_COURSES}/${courseName}/${assignmentId}/${homeworkId}/${correctionId}/correction`);
  }

  getMaxResources(courseName: string) {
    return this.http.get<any>(`${this.API_COURSES}/${courseName}/maxResources`);
  }

}
