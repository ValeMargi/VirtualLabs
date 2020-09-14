import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserNotAllowedComponent } from './user-not-allowed.component';

describe('UserNotAllowedComponent', () => {
  let component: UserNotAllowedComponent;
  let fixture: ComponentFixture<UserNotAllowedComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserNotAllowedComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserNotAllowedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
