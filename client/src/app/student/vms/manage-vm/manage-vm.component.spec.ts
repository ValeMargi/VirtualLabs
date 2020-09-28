import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageVmComponent } from './manage-vm.component';

describe('ManageVmComponent', () => {
  let component: ManageVmComponent;
  let fixture: ComponentFixture<ManageVmComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ManageVmComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ManageVmComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
