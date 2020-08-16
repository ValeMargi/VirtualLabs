import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddCourseContComponent } from './add-course-cont.component';

describe('AddCourseContComponent', () => {
  let component: AddCourseContComponent;
  let fixture: ComponentFixture<AddCourseContComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddCourseContComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddCourseContComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
