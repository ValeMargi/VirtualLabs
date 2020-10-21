import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadCorrectionComponent } from './upload-correction.component';

describe('UploadCorrectionComponent', () => {
  let component: UploadCorrectionComponent;
  let fixture: ComponentFixture<UploadCorrectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UploadCorrectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UploadCorrectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
