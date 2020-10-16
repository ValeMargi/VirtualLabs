import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit, OnChanges {

  @Input() ok: boolean;
  @Input() error: boolean;
  @Input() msg: string;
  @Output('change') change = new EventEmitter<string>(); 

  constructor() { }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.ok != null) {
      this.ok = changes.ok.currentValue;
    }

    if (changes.error != null) {
      this.error = changes.error.currentValue;
    }

    if (changes.msg != null) {
      this.msg = changes.msg.currentValue;
    }
  }

  save(newPass: string, passR: string) {
    if (newPass.length == 0 || passR.length == 0) {
      window.alert("Riempire entrambi i campi");
    }
    else if (newPass != passR) {
      window.alert("Le 2 password devono coincidere");
    }

    this.change.emit(newPass);
  }

}
