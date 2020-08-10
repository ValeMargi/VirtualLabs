import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateVmsContComponent } from './create-vms-cont.component';

describe('CreateVmsContComponent', () => {
  let component: CreateVmsContComponent;
  let fixture: ComponentFixture<CreateVmsContComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CreateVmsContComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateVmsContComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
