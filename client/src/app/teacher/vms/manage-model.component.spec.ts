import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageModelComponent } from './manage-model.component';

describe('ManageModelComponent', () => {
  let component: ManageModelComponent;
  let fixture: ComponentFixture<ManageModelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ManageModelComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ManageModelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
