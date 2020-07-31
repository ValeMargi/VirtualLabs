import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestTeamDialogContComponent } from './request-team-dialog-cont.component';

describe('RequestTeamDialogContComponent', () => {
  let component: RequestTeamDialogContComponent;
  let fixture: ComponentFixture<RequestTeamDialogContComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RequestTeamDialogContComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestTeamDialogContComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
