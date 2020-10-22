import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-upload-correction',
  templateUrl: './upload-correction.component.html',
  styleUrls: ['./upload-correction.component.css']
})
export class UploadCorrectionComponent implements OnInit {
  private selectedPhoto: File;

  @Input() querying: boolean;
  @Output('upload') upload = new EventEmitter<any>();

  constructor(private matDialoRef: MatDialogRef<UploadCorrectionComponent>) { }

  ngOnInit(): void {
  }

  onFileChanged(imageInput) {
    this.selectedPhoto = imageInput.target.files[0];
  }

  uploadCorrection(grade: string) {
    if (grade.length > 0 && Number.parseInt(grade) < 0 || Number.parseInt(grade) > 30) {
      window.alert("Inserire un voto tra 0 e 30");
    }
    else if (this.selectedPhoto == null) {
      window.alert("Inserire un'immagine");
    }
    else {
      this.upload.emit({file: this.selectedPhoto, grade: grade});
    }
  }

  close() {
    this.matDialoRef.close();
  }

}
