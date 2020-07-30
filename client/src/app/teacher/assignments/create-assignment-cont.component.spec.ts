import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateAssignmentContComponent } from './create-assignment-cont.component';

describe('CreateAssignmentContComponent', () => {
  let component: CreateAssignmentContComponent;
  let fixture: ComponentFixture<CreateAssignmentContComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CreateAssignmentContComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateAssignmentContComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
