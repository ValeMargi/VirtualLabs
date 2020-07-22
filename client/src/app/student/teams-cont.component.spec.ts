import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TeamsContComponent } from './teams-cont.component';

describe('TeamsContComponent', () => {
  let component: TeamsContComponent;
  let fixture: ComponentFixture<TeamsContComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TeamsContComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TeamsContComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
