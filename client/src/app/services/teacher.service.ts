import { Injectable, EventEmitter } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Teacher } from '../models/teacher.model';
import { map } from 'rxjs/operators';
import { Course } from '../models/course.model';
import { VM } from '../models/vm.model';
import { Student } from '../models/student.model';
import { Assignment } from '../models/assignment.model';
import { PhotoAssignment } from '../models/photo-assignment.model';
import { Homework } from '../models/homework.model';

@Injectable({
  providedIn: 'root'
})
export class TeacherService {

  API_PROFESSORS = "http://localhost:8080/API/professors";

  currentTeacher: Teacher;
  currentAvatar: any;

  assCreation: EventEmitter<Assignment> = new EventEmitter<Assignment>();
  
  constructor(private http: HttpClient) { }

  all() {
    return this.http.get<Teacher[]>(`${this.API_PROFESSORS}`).pipe(map(teachers => teachers || []));
  }

  getOne(professorId: string) {
    //ritorna professore e avatar
    return this.http.get<any>(`${this.API_PROFESSORS}/${professorId}`);
  }

  getCoursesForProfessor(professorId: string) {
    return this.http.get<Course[]>(`${this.API_PROFESSORS}/${professorId}/courses`).pipe(map(courses => courses || []));
  }

  addModelVM(courseName: string, file: File, course: Course) {
    let data: FormData = new FormData();
    data.append("file", file, file.name);
    data.append("modelVM", new Blob([JSON.stringify(course)], {
      type: "application/json" }));

    return this.http.post<Course>(`${this.API_PROFESSORS}/${courseName}/addModel`, data);
  }

  updateModelVM(courseName: string, course: Course) {
    let data: FormData = new FormData();
    data.append("modelVM", new Blob([JSON.stringify(course)], {
      type: "application/json" }));

    return this.http.post<Course>(`${this.API_PROFESSORS}/${courseName}/update`, data);
  }

  allVMforCourse(courseName: string) {
    return this.http.get<VM[]>(`${this.API_PROFESSORS}/VM/${courseName}`).pipe(map(vms => vms || []));
  }

  getOwners(courseName: string, teamId: number, vmId: number) {
    return this.http.get<Student[]>(`${this.API_PROFESSORS}/VM/${courseName}/${teamId}/${vmId}`).pipe(map(students => students || []));
  }

  addAssignment(courseName: string, file: File, assignment: Assignment) {
    let data: FormData = new FormData();
    data.append("file", file, file.name);
    data.append("assignment", new Blob([JSON.stringify(assignment)], {
      type: "application/json" }));

    return this.http.post(`${this.API_PROFESSORS}/${courseName}/addAssignment`, data);
  }

  allAssignments(courseName: string) {
    return this.http.get<Assignment[]>(`${this.API_PROFESSORS}/${courseName}/assignments`).pipe(map(ass => ass || []));
  }

  getPhotoAssignment(courseName: string, assignmentId: number) {
    return this.http.get<PhotoAssignment>(`${this.API_PROFESSORS}/${courseName}/${assignmentId}/getAssignment`);
  }

  allHomework(courseName: string, assignmentId: number) {
    return this.http.get<Homework[]>(`${this.API_PROFESSORS}/${courseName}/${assignmentId}/allHomework`).pipe(map(homeworks => homeworks || []));
  }

  getVersionsHMForProfessor(courseName: string, assignmentId: number, homeworkId: number) {
    return this.http.get(`${this.API_PROFESSORS}/${courseName}/${assignmentId}/${homeworkId}/getVersions`).pipe(map(versions => versions || []));
  }

  getCorrectionsHMForProfessor(courseName: string, assignmentId: number, homeworkId: number) {
    return this.http.get(`${this.API_PROFESSORS}/${courseName}/${assignmentId}/${homeworkId}/getCorrections`).pipe(map(corrections => corrections || []));
  }

  uploadCorrection(courseName: string, assignmentId: number, homeworkId: number, versionHMid, file: File, permanent: boolean, grade: string) {
    let data: FormData = new FormData();
    data.append("file", file, file.name);
    data.append("permanent", permanent.toString());
    data.append("grade", grade);

    return this.http.post(`${this.API_PROFESSORS}/${courseName}/${assignmentId}/${homeworkId}/${versionHMid}/uploadCorrection`, data);
  }


}
