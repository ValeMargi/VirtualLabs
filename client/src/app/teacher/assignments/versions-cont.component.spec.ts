import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VersionsContComponent } from './versions-cont.component';

describe('VersionsContComponent', () => {
  let component: VersionsContComponent;
  let fixture: ComponentFixture<VersionsContComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VersionsContComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VersionsContComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
