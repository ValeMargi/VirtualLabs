import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageModelContComponent } from './manage-model-cont.component';

describe('ManageModelContComponent', () => {
  let component: ManageModelContComponent;
  let fixture: ComponentFixture<ManageModelContComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ManageModelContComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ManageModelContComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
