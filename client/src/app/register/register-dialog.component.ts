import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { MatDialog, MatDialogRef, MatDialogConfig } from '@angular/material/dialog';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';
import { LoginDialogComponent } from '../login/login-dialog.component';
import { FormControl, Validators, FormGroup, FormBuilder } from '@angular/forms';


@Component({
  selector: 'app-register-dialog',
  templateUrl: './register-dialog.component.html',
  styleUrls: ['./register-dialog.component.css']
})
export class RegisterDialogComponent implements OnInit {
  @ViewChild('avatar') avatar: ElementRef

  RegisterForm: FormGroup;
  selectedPhoto: File;

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
      password: ['', [Validators.required,Validators.minLength(8)]],
      confirmPassword: ['',[Validators.minLength(8)]],
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

  this.matDialog.open(LoginDialogComponent, dialogConfig);
}

register(firstName: string, name: string, id: string, email: string, password: string) {
  if (this.selectedPhoto == null) {
    return;
  }

  let image = this.selectedPhoto;

  if (image.type.match("image/jpg") || image.type.match("image/jpeg") || image.type.match("image/png")) {
    console.log("tipo errato");
    //mostrare errore
  }

  let userMap = new Map<string, string>();
  userMap.set("firstName", firstName);
  userMap.set("name", name);
  userMap.set("id", id);
  userMap.set("email", email)
  userMap.set("password", password)

  this.authService.registerUser(image, userMap);
}

onFileChanged(imageInput) {
  this.selectedPhoto = imageInput.files[0]
  
  const reader = new FileReader();

    /*reader.addEventListener('load', (event: any) => {
      let snippet = new ImageS(event.target.result, this.selectedPhoto);
    });

    reader.readAsDataURL(this.selectedPhoto);*/
}

}
