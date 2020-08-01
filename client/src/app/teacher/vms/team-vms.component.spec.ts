import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TeamVmsComponent } from './team-vms.component';

describe('TeamVmsComponent', () => {
  let component: TeamVmsComponent;
  let fixture: ComponentFixture<TeamVmsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TeamVmsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TeamVmsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
