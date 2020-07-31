import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddHomeworkContComponent } from './add-homework-cont.component';

describe('AddHomeworkContComponent', () => {
  let component: AddHomeworkContComponent;
  let fixture: ComponentFixture<AddHomeworkContComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddHomeworkContComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddHomeworkContComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
