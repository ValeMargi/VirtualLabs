import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class TeacherService {

  API_COURSES = "http://localhost:8080/courses";

  constructor(private http: HttpClient) { }

  
}
