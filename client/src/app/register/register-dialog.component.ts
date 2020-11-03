import { Component, OnInit, ViewChild, ElementRef, Output, EventEmitter, Input, OnChanges, SimpleChanges } from '@angular/core';
import { MatDialog, MatDialogRef, MatDialogConfig } from '@angular/material/dialog';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { LoginDialogComponent } from '../login/login-dialog.component';
import { FormControl, Validators, FormGroup, FormBuilder } from '@angular/forms';
import { LoginContComponent } from '../login/login-cont/login-cont.component';
import { Md5 } from 'ts-md5/dist/md5';


@Component({
  selector: 'app-register-dialog',
  templateUrl: './register-dialog.component.html',
  styleUrls: ['./register-dialog.component.css']
})
export class RegisterDialogComponent implements OnInit, OnChanges {

  RegisterForm: FormGroup;
  selectedPhoto: File;
  previewPhoto: any;

  avatarImg: boolean = true;

  private md5: Md5;

  @Input() querying: boolean;
  @Input() ok: boolean;
  @Output('register') reg = new EventEmitter<any>();

  constructor(
    public matDialog: MatDialog,
    public authService: AuthService,
    private dialogRef: MatDialogRef<RegisterDialogComponent>,
    private router: Router,
    private formBuilder: FormBuilder) {

    this.RegisterForm = this.formBuilder.group({
      name : new FormControl('', [Validators.required, Validators.minLength(3)]),
      surname : new FormControl('', [Validators.required]),
      id : new FormControl('', [Validators.required]),
      email: new FormControl('',[Validators.email,this.emailDomainValidator]),
      password: ['', [Validators.required,Validators.minLength(8), Validators.maxLength(20)]],
      confirmPassword: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(20)]],
    }, { validator: this.checkPasswords });

}

emailDomainValidator(control: FormControl) {
  let email = control.value;
  if (email && email.indexOf("@") != -1) {
    let [_, domain] = email.split("@");
    if (domain !== "studenti.polito.it" && domain !== "polito.it") {
      return {
        emailDomain: {
          parsedDomain: domain
        }
      }
    }
  }
  return null;
}

checkPasswords(group: FormGroup) { // here we have the 'passwords' group
    let pass = group.controls.password.value;
    let confirmPass = group.controls.confirmPassword.value;

    return pass === confirmPass ? null : { notSame: true }
  }

ngOnInit() {
}

ngOnChanges(changes: SimpleChanges) {
  if (changes.querying != null) {
    this.querying = changes.querying.currentValue;
  }

  if (changes.ok != null) {
    this.ok = changes.ok.currentValue;
  }
}

forceLower(strInput: string)
{
strInput = strInput.toLowerCase();
}​

close() {
  this.dialogRef.close();
}

openDialogLogin() {
  this.dialogRef.close();

  const dialogConfig = new MatDialogConfig();

  dialogConfig.disableClose = false;
  dialogConfig.autoFocus = true;

  dialogConfig.data = {
      id: 1,
      title: 'Login'
  };

  this.matDialog.open(LoginContComponent, dialogConfig);
}

register(firstName: string, name: string, id: string, email: string, password: string, pwdRepeat: string) {
  if (this.selectedPhoto == null) {
    window.alert("Selezionare una foto del profilo");
    return;
  }
  else if (password != pwdRepeat) {
    window.alert("Le 2 password devono coincidere");
    return;
  }
  else if (!this.RegisterForm.valid) {
    window.alert("Controllare che i dati inseriti rispettino tutti i vincoli e riprovare");
    return;
  }

  let image = this.selectedPhoto;

  if (!image.type.match("image/jpg") && !image.type.match("image/jpeg") && !image.type.match("image/png")) {
    window.alert("Formato immagine non supportato");
    return;
  }

  this.md5 = new Md5();

  let userJson = { "firstName": firstName.charAt(0).toUpperCase() + firstName.toLowerCase().slice(1),
                  "name": name.charAt(0).toUpperCase() + name.toLowerCase().slice(1),
                  "id": id.toLowerCase(),
                  "email": email.toLowerCase(),
                  "password": this.md5.start().appendStr(password).end().toString()
                };

  let idEmail: string[] = userJson.email.split("@");

  if (userJson.id != idEmail[0]){
    window.alert("La tua email non corrisponde alla matricola inserita");
    return;
  }
  /*
  else if (!this.isEmailStudent(userJson.email)) {
    window.alert("Lo studente deve avere come dominio 'studenti.polito.it'");
    return;
  }
  else if (!this.isEmailTeacher(userJson.email)) {
    window.alert("Il docente deve avere come dominio 'polito.it'");
    return;
  }
*/
  this.reg.emit({image: image, userJson: userJson});
}


isEmailStudent(emailUser:string):boolean
{
    var  correct:boolean;
    let regexp = new RegExp('([sS]{1}[0-9]+[@]{1}[sS]{1}[tT]{1}[uU]{1}[dD]{1}[eE]{1}[nN]{1}[tT]{1}[iI]{1}[.][pP]{1}[oO]{1}[Ll]{1}[iI]{1}[tT]{1}[oO][.][iI]{1}[tT]{1})');
    correct = regexp.test(emailUser);

    console.log(correct);
    return correct;
}

isEmailTeacher(emailUser:string):boolean
{
    var  correct:boolean;
    let regexp = new RegExp('([dD]{1}[0-9]+[@]{1}[pP]{1}[oO]{1}[Ll]{1}[iI]{1}[tT]{1}[oO][.][iI]{1}[tT]{1})');
    correct = regexp.test(emailUser);

    console.log(correct);
    return correct;
}


onFileChanged(imageInput) {
  this.selectedPhoto = imageInput.target.files[0]

  const reader = new FileReader();
  reader.readAsDataURL(this.selectedPhoto);
  reader.onload = (_event) => {
    this.avatarImg = false;
    this.previewPhoto = reader.result;
  }
}

}
