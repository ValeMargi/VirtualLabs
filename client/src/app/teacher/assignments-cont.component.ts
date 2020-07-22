import { Component, OnInit } from '@angular/core';
import { Assignment } from '../assignment.model';
import { Delivery } from '../delivery.model';
import { AssignmentsService } from '../services/assignments.service';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-assignments-cont',
  templateUrl: './assignments-cont.component.html',
  styleUrls: ['./assignments-cont.component.css']
})
export class AssignmentsContComponent implements OnInit {

  public DELIVERIES: Delivery[] = []
  public ASSIGNMENTS: Assignment[] = []

  constructor(public assService: AssignmentsService, public authService: AuthService) { 
    
  }

  ngOnInit(): void {
  }

}
