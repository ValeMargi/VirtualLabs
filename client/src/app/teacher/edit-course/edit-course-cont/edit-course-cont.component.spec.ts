import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditCourseContComponent } from './edit-course-cont.component';

describe('EditCourseContComponent', () => {
  let component: EditCourseContComponent;
  let fixture: ComponentFixture<EditCourseContComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditCourseContComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditCourseContComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
