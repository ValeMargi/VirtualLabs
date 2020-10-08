import { Component, Input, OnChanges, OnInit, SimpleChange, SimpleChanges } from '@angular/core';
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

  constructor(private matDialogRef: MatDialogRef<ViewImageComponent>) { }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges) {
    this.photo = changes.photo.currentValue;
    this.timestamp = changes.timestamp.currentValue;
  }

  close() {
    this.matDialogRef.close();
  }

}
