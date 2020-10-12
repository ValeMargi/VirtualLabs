import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ForgotPasswordContComponent } from './forgot-password-cont.component';

describe('ForgotPasswordContComponent', () => {
  let component: ForgotPasswordContComponent;
  let fixture: ComponentFixture<ForgotPasswordContComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ForgotPasswordContComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ForgotPasswordContComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
