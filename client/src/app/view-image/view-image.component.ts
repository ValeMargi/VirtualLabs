import { Component, Input, OnChanges, OnInit, Output, EventEmitter, SimpleChanges } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-view-image',
  templateUrl: './view-image.component.html',
  styleUrls: ['./view-image.component.css']
})
export class ViewImageComponent implements OnInit, OnChanges {

  @Input() photo: any;
  @Input() title: string;
  @Input() timestamp: string;
  @Input() VMStudent: boolean;
  @Input() querying: boolean;
  @Output('use') use = new EventEmitter<File>();

  private selectedPhoto: File;

  constructor(private matDialogRef: MatDialogRef<ViewImageComponent>) { }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.photo != null) {
      this.photo = changes.photo.currentValue;
    }
    
    if (changes.timestamp != null) {
      this.timestamp = changes.timestamp.currentValue;
    }

    if (changes.VMStudent != null) {
      this.VMStudent = changes.VMStudent.currentValue;
    }

    if (changes.querying != null) {
      this.querying = changes.querying.currentValue;
    }
  }

  onFileChanged(imageInput) {
    this.selectedPhoto = imageInput.target.files[0]

    if (this.selectedPhoto != null) {
      this.use.emit(this.selectedPhoto);
    }
  }

  close() {
    this.matDialogRef.close();
  }

}
