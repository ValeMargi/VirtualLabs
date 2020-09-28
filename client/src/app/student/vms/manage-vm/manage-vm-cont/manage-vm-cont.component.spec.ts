import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageVmContComponent } from './manage-vm-cont.component';

describe('ManageVmContComponent', () => {
  let component: ManageVmContComponent;
  let fixture: ComponentFixture<ManageVmContComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ManageVmContComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ManageVmContComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
