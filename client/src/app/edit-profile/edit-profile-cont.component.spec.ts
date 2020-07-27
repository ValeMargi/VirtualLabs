import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditProfileContComponent } from './edit-profile-cont.component';

describe('EditProfileContComponent', () => {
  let component: EditProfileContComponent;
  let fixture: ComponentFixture<EditProfileContComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditProfileContComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditProfileContComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
