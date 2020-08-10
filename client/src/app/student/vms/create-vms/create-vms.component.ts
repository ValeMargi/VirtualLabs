import { Component, OnInit, AfterViewChecked, AfterViewInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
@Component({
  selector: 'app-create-vms',
  templateUrl: './create-vms.component.html',
  styleUrls: ['./create-vms.component.css']
})
export class CreateVmsComponent implements OnInit {

  constructor(private matDialogRef: MatDialogRef<CreateVmsComponent>) { }

  ngOnInit(): void {
  }


  close() {
    this.matDialogRef.close();
  }

  createVms() {
    
  }
}
