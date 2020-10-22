import { Component, OnInit, Input,Output,EventEmitter } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import * as moment from 'moment';
@Component({
  selector: 'app-request-team-dialog',
  templateUrl: './request-team-dialog.component.html',
  styleUrls: ['./request-team-dialog.component.css']
})
export class RequestTeamDialogComponent implements OnInit{

  @Output('propose') propose = new EventEmitter<any>();

  form = {
    name : new FormControl('', [Validators.required, Validators.minLength(3)]),
    date : new FormControl('', [Validators.required]),
    members: new FormControl('', [Validators.required])
  }

  constructor() {}
  ngOnInit() {}

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
