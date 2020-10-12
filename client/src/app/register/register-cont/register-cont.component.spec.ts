import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterContComponent } from './register-cont.component';

describe('RegisterContComponent', () => {
  let component: RegisterContComponent;
  let fixture: ComponentFixture<RegisterContComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RegisterContComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterContComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
