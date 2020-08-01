import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeworksContComponent } from './homeworks-cont.component';

describe('HomeworksContComponent', () => {
  let component: HomeworksContComponent;
  let fixture: ComponentFixture<HomeworksContComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HomeworksContComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HomeworksContComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
