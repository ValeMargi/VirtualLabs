import { Component, OnInit } from '@angular/core';
import { AssignmentsService } from '../../services/assignments.service';
import { AuthService } from '../../auth/auth.service';
import { Homework } from 'src/app/models/homework.model';
import { Assignment } from 'src/app/models/assignment.model';

@Component({
  selector: 'app-assignments-cont',
  templateUrl: './assignments-cont.component.html',
  styleUrls: ['./assignments-cont.component.css']
})
export class AssignmentsContComponent implements OnInit {

  public HOMEWORKS: Homework[] = []
  public ASSIGNMENTS: Assignment[] = []

  constructor(public assService: AssignmentsService, public authService: AuthService) { 
    
  }

  ngOnInit(): void {
  }

}
