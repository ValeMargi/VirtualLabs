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
import { HomeworkVersion } from '../models/homework-version.model';
import { HomeworkCorrection } from '../models/homework-correction.model';
import { PhotoVM } from '../models/photo-vm.model';

@Injectable({
  providedIn: 'root'
})
export class TeacherService {

  API_PROFESSORS = "http://localhost:8080/API/professors";

  currentTeacher: Teacher;  //professore loggato
  currentAvatar: any;  //foto profilo del professore loggato

  assCreation: EventEmitter<Assignment> = new EventEmitter<Assignment>();  //emitter per notificare la creazione di una consegna
  corrUpload: EventEmitter<any> = new EventEmitter<any>();  //emitter per notificare l'inserimento di una correzione howework
  
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

  getVMForProfessor(courseName: string, vmId: number) {
    return this.http.get<PhotoVM>(`${this.API_PROFESSORS}/VM/${courseName}/${vmId}`);
  }

  getResourcesVM(teamId: number) {
    return this.http.get<any>(`${this.API_PROFESSORS}/team/${teamId}/resources`)
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

    return this.http.post<Assignment>(`${this.API_PROFESSORS}/${courseName}/addAssignment`, data);
  }

  allAssignments(courseName: string) {
    return this.http.get<Assignment[]>(`${this.API_PROFESSORS}/${courseName}/assignments`).pipe(map(ass => ass || []));
  }

  getAssignment(courseName: string, assignmentId: number) {
    return this.http.get<Assignment>(`${this.API_PROFESSORS}/${courseName}/${assignmentId}/getAssignmentDTO`);
  }

  getPhotoAssignment(courseName: string, assignmentId: number) {
    return this.http.get<PhotoAssignment>(`${this.API_PROFESSORS}/${courseName}/${assignmentId}/getAssignment`);
  }

  allHomework(courseName: string, assignmentId: number) {
    return this.http.get<any[]>(`${this.API_PROFESSORS}/${courseName}/${assignmentId}/allHomework`).pipe(map(homeworks => homeworks || []));
  }

  getVersionsHMForProfessor(courseName: string, assignmentId: number, homeworkId: number) {
    return this.http.get<HomeworkVersion[]>(`${this.API_PROFESSORS}/${courseName}/${assignmentId}/${homeworkId}/getVersions`).pipe(map(versions => versions || []));
  }

  getCorrectionsHMForProfessor(courseName: string, assignmentId: number, homeworkId: number) {
    return this.http.get<HomeworkCorrection[]>(`${this.API_PROFESSORS}/${courseName}/${assignmentId}/${homeworkId}/getCorrections`).pipe(map(corrections => corrections || []));
  }

  uploadCorrection(courseName: string, assignmentId: number, homeworkId: number, versionHMid: number, file: File, permanent: boolean, grade: string) {
    let data: FormData = new FormData();
    data.append("file", file, file.name);
    //grade e permanent vanno di pari passo => se permanent = false, metto grade = -1
    data.append("permanent", permanent.toString());
    data.append("grade", grade);

    return this.http.post<HomeworkCorrection>(`${this.API_PROFESSORS}/${courseName}/${assignmentId}/${homeworkId}/${versionHMid}/uploadCorrection`, data);
  }


}
