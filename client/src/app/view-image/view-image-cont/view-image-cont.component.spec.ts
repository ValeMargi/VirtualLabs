import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewImageContComponent } from './view-image-cont.component';

describe('ViewImageContComponent', () => {
  let component: ViewImageContComponent;
  let fixture: ComponentFixture<ViewImageContComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewImageContComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewImageContComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
