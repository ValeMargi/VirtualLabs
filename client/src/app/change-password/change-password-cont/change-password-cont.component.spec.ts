import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangePasswordContComponent } from './change-password-cont.component';

describe('ChangePasswordContComponent', () => {
  let component: ChangePasswordContComponent;
  let fixture: ComponentFixture<ChangePasswordContComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChangePasswordContComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChangePasswordContComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
