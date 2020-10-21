import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadCorrectionContComponent } from './upload-correction-cont.component';

describe('UploadCorrectionContComponent', () => {
  let component: UploadCorrectionContComponent;
  let fixture: ComponentFixture<UploadCorrectionContComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UploadCorrectionContComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UploadCorrectionContComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
