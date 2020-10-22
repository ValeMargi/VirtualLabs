import { Component, OnInit, Input,Output,EventEmitter, OnChanges,SimpleChanges } from '@angular/core';
import { ViewChild, AfterViewInit } from '@angular/core';
import { MatTable } from '@angular/material/table';
import { FormControl, Validators } from '@angular/forms';
import { Student } from '../../../models/student.model';
import { Observable } from 'rxjs';
import { map, startWith} from 'rxjs/operators';
import { SelectionModel } from '@angular/cdk/collections';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatInput } from '@angular/material/input';
import { MatTableDataSource } from '@angular/material/table';
import { CourseService } from 'src/app/services/course.service';
import { Router, ActivatedRoute } from '@angular/router';
import * as moment from 'moment';

@Component({
  selector: 'app-request-team-dialog',
  templateUrl: './request-team-dialog.component.html',
  styleUrls: ['./request-team-dialog.component.css']
})
export class RequestTeamDialogComponent implements AfterViewInit,OnInit,OnChanges{

  @Output('propose') propose = new EventEmitter<any>();

  form = {
    name : new FormControl('', [Validators.required, Validators.minLength(3)]),
    date : new FormControl('', [Validators.required]),
    members: new FormControl('', [Validators.required])
  }

  constructor(
    private courseService: CourseService,
    private router: Router,
    private route: ActivatedRoute) {}

  ngAfterViewInit(): void {}
  ngOnInit() {}
  ngOnChanges(changes: SimpleChanges) {}

  getErrorMessage() {
    if (this.form.name.hasError('required') || this.form.date.hasError('required')) {
      return 'Campo obbligatorio';
    }
    if(this.form.name.hasError('minlength')){
      return 'Inserire almeno 3 caratteri';
    }
  }

  proposeTeam(nameTeam: string, days: string, members: string) {
    let timeout: string = moment(new Date().setDate(new Date().getDate() + Number.parseInt(days))).format("YYYY-MM-DD HH:mm:ss.SSS");
    let membersId: string[] = members.split(",");
    this.propose.emit({teamName: nameTeam, timeout: timeout, membersId: membersId});
  }

}
