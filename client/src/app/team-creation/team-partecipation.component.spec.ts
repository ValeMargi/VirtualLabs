import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TeamPartecipationComponent } from './team-partecipation.component';

describe('TeamPartecipationComponent', () => {
  let component: TeamPartecipationComponent;
  let fixture: ComponentFixture<TeamPartecipationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TeamPartecipationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TeamPartecipationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
