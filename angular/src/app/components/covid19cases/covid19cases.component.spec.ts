import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Covid19casesComponent } from './covid19cases.component';

describe('XyzComponent', () => {
  let component: Covid19casesComponent;
  let fixture: ComponentFixture<Covid19casesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ Covid19casesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Covid19casesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
