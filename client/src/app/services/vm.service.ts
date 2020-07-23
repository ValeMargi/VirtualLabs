import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../auth/auth.service';
import { Observable, from } from 'rxjs';
import { VM } from '../models/vm.model';
import { map, concatMap, toArray } from 'rxjs/operators';
import { Student } from '../models/student.model';

@Injectable({
  providedIn: 'root'
})
export class VmService {

  constructor(private http: HttpClient) {}

  MY_PAHT = "http://localhost:3000/VMs";

  create(VM: VM) {
    return this.http.post<VM>(this.MY_PAHT, VM);
  }

  update(VM: VM) {
    return this.http.put<VM>(this.MY_PAHT, VM);
  }

  find(id: string): Observable<VM> {
    return this.http.get<VM>(`${this.MY_PAHT}/${id}`);
  }

  query(): Observable<VM[]> {
    return this.http.get<VM[]>(`${this.MY_PAHT}`)
                    .pipe(map(VMs => VMs || []));
  }

  delete(id: string) {
    return this.http.delete(`${this.MY_PAHT}/${id}`);
  }

  addStudents(vm: VM, students: Student[]) {
    students.forEach(student => {
      vm.studentIds.push(student.id);
    });

    return this.http.put<VM>(`${this.MY_PAHT}/${vm.id}`, vm);
  }

  removeStudents(vm: VM, students: Student[]) {
    students.forEach(student => {
      vm.studentIds.splice(vm.studentIds.indexOf(student.id), 1);
    });

    return this.http.put<VM>(`${this.MY_PAHT}/${vm.id}`, vm);
  }
}
