import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestTeamDialogComponent } from './request-team-dialog.component';

describe('RequestTeamDialogComponent', () => {
  let component: RequestTeamDialogComponent;
  let fixture: ComponentFixture<RequestTeamDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RequestTeamDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestTeamDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
