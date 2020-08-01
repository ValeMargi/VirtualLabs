import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TeamVmsContComponent } from './team-vms-cont.component';

describe('TeamVmsContComponent', () => {
  let component: TeamVmsContComponent;
  let fixture: ComponentFixture<TeamVmsContComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TeamVmsContComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TeamVmsContComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
