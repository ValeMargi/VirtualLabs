import { Component, OnInit, Input,Output,EventEmitter } from '@angular/core';
import { FormControl, FormGroup, Validators, FormBuilder } from '@angular/forms';
import * as moment from 'moment';
@Component({
  selector: 'app-request-team-dialog',
  templateUrl: './request-team-dialog.component.html',
  styleUrls: ['./request-team-dialog.component.css']
})
export class RequestTeamDialogComponent implements OnInit{

  timeouts = [
    {value: "7", viewValue: "1 settimana"},
    {value: "14", viewValue: "2 settimane"}
  ];

  defaultTimeout = this.timeouts[0].value;
  CreateTeamForm: FormGroup;

  @Output('propose') propose = new EventEmitter<any>();

  constructor(private formBuilder: FormBuilder) {
    this.CreateTeamForm = formBuilder.group({
      name : new FormControl('', [Validators.required, Validators.minLength(3)]),
      date : new FormControl('', [Validators.required]),
      members: new FormControl('', [Validators.required])
    });
  }

  ngOnInit() {}

  proposeTeam(nameTeam: string, days: string, members: string) {
    if (this.CreateTeamForm.valid) {
      let timeout: string = moment(new Date().setDate(new Date().getDate() + Number.parseInt(days))).format("YYYY-MM-DD HH:mm:ss.SSS");
      let membersToTrim: string[] = members.split(",");
      let membersId: string[] = [];

      membersToTrim.forEach(m => {
        membersId.push(m.trim());
      });

      this.propose.emit({teamName: nameTeam, timeout: timeout, membersId: membersId});
    }
    else {
      window.alert("Controllare di aver inserito dei campi validi e riprovare");
    }
  }

}
