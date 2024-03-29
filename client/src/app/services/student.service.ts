import { EventEmitter, Injectable } from '@angular/core';
import { Student } from '../models/student.model';
import { of, Observable, from } from 'rxjs';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { map, switchMap, concatMap, toArray } from 'rxjs/operators';
import { AuthService } from '../auth/auth.service';
import { Course } from '../models/course.model';
import { Team } from '../models/team.model';
import { VM } from '../models/vm.model';
import { PhotoVM } from '../models/photo-vm.model';
import { Assignment } from '../models/assignment.model';
import { PhotoAssignment } from '../models/photo-assignment.model';
import { Homework } from '../models/homework.model';
import { HomeworkVersion } from '../models/homework-version.model';
import { HomeworkCorrection } from '../models/homework-correction.model';
import { VMOwners } from '../models/vm-owners.model';

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  constructor(private http: HttpClient) {}

  API_STUDENTS = "http://localhost:8080/API/students";

  currentStudent: Student;  //studente loggato
  currentAvatar: File;  //foto profilo dello studente loggato

  vmCreation: EventEmitter<VMOwners> = new EventEmitter<VMOwners>();  //emitter per notificare la creazione di una vm
  vmDelete: EventEmitter<VMOwners> = new EventEmitter<VMOwners>();  //emitter per notificare l'eliminazione di una vm
  verUpload: EventEmitter<HomeworkVersion> = new EventEmitter<HomeworkVersion>(); //emitter per notificare l'aggiunta di una versione homework

  all() {
    return this.http.get<Student[]>(`${this.API_STUDENTS}`).pipe(map(students => students || []));
  }

  getOne(studentId: string) {
    //ritorna studente e avatar
    return this.http.get<any>(`${this.API_STUDENTS}/${studentId}`);
  }

  getCourses(studentId: string) {
    return this.http.get<Course[]>(`${this.API_STUDENTS}/${studentId}/courses`).pipe(map(courses => courses || []));
  }

  allVMsForStudent(courseName: string) {
    return this.http.get<VM[]>(`${this.API_STUDENTS}/VM/${courseName}`).pipe(map(vms => vms || []));
  }

  getVMForStudent(courseName: string, VMId: number) {
    return this.http.get<PhotoVM>(`${this.API_STUDENTS}/VM/${courseName}/${VMId}`);
  }

  isOwner(courseName: string, VMId: number) {
    return this.http.get<boolean>(`${this.API_STUDENTS}/VM/${courseName}/${VMId}/owner`);
  }

  allAssignments(courseName: string) {
    return this.http.get<any[]>(`${this.API_STUDENTS}/${courseName}/assignment`).pipe(map(ass => ass || []));
  }

  allHomework(courseName: string, assignmentId: number) {
    return this.http.get<Homework[]>(`${this.API_STUDENTS}/${courseName}/${assignmentId}/allHomework`).pipe(map(homeworks => homeworks || []));
  }

  getAssignment(courseName: string, assignmentId: number) {
    return this.http.get<PhotoAssignment>(`${this.API_STUDENTS}/${courseName}/${assignmentId}/getAssignment`)
  }

  addVM(courseName: string, vm: VM) {
    let data: FormData = new FormData();
    data.append("VM", new Blob([JSON.stringify(vm)], {
      type: "application/json" }));

    return this.http.post<VM>(`${this.API_STUDENTS}/${courseName}/addVM`, data);
  }

  addOwners(courseName: string, VMId: number, membersId: string[]) {
    return this.http.post<boolean>(`${this.API_STUDENTS}/${courseName}/${VMId}/addOwner`, membersId);
  }

  getOwners(courseName: string, teamId: number, vmId: number) {
    return this.http.get<Student[]>(`${this.API_STUDENTS}/VM/${courseName}/${teamId}/${vmId}`).pipe(map(students => students || []));
  }

  activateVM(courseName: string, VMId: number) {
    return this.http.get<boolean>(`${this.API_STUDENTS}/${courseName}/${VMId}/activateVM`);
  }

  disableVM(courseName: string, VMId: number) {
    return this.http.get<boolean>(`${this.API_STUDENTS}/${courseName}/${VMId}/disableVM`);
  }

  removeVM(courseName: string, VMId: number) {
    return this.http.get<boolean>(`${this.API_STUDENTS}/${courseName}/${VMId}/removeVM`);
  }

  useVM(courseName: string, VMId: number, file: File) {
    let data: FormData = new FormData();
    data.append("file", file, file.name);

    return this.http.post<boolean>(`${this.API_STUDENTS}/${courseName}/${VMId}/use`, data);
  }

  updateVMresources(courseName: string, VMId: number, vm: VM) {
    let data: FormData = new FormData();
    data.append("VM", new Blob([JSON.stringify(vm)], {
      type: "application/json" }));

    return this.http.post<VM>(`${this.API_STUDENTS}/${courseName}/${VMId}/update`, data);
  }

  uploadVersionHomework(courseName: string, assignmentId: number, homeworkId: number, file: File) {
    let data: FormData = new FormData();
    data.append("file", file, file.name);

    return this.http.post<HomeworkVersion>(`${this.API_STUDENTS}/${courseName}/${assignmentId}/${homeworkId}/uploadHomework`, data);
  }

  getHomework(courseName: string, assignmentId: number) {
    return this.http.get<Homework>(`${this.API_STUDENTS}/${courseName}/${assignmentId}/getHomework`);
  }

  getVersionsHMForStudent(courseName: string, assignmentId: number) {
    return this.http.get<HomeworkVersion[]>(`${this.API_STUDENTS}/${courseName}/${assignmentId}/getVersions`).pipe(map(versions => versions || []));
  }

  getCorrectionsHMForStudent(courseName: string, assignmentId: number) {
    return this.http.get<HomeworkCorrection[]>(`${this.API_STUDENTS}/${courseName}/${assignmentId}/getCorrections`).pipe(map(corrections => corrections || []));
  }

}
