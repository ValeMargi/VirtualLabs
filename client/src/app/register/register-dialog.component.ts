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

    authService.userLogged.subscribe(ok => {
      if (ok && authService.isLoggedIn()) {

        if (router.url == "/")
          router.navigateByUrl("home");
      }
      else {
        document.getElementById("error").style.visibility = "visible";
      }
    });

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
  }
  else if (userJson.id.startsWith("s") && idEmail[1] == "polito.it") {
    window.alert("Lo studente deve avere come dominio 'studenti.polito.it'");
  }
  else if (userJson.id.startsWith("d") && idEmail[1] == "studenti.polito.it") {
    window.alert("Il docente deve avere come dominio 'polito.it'");
  }

  this.reg.emit({image: image, userJson: userJson});
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
