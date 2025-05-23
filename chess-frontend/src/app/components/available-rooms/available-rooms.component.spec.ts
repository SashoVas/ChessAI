import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AvailableRoomsComponent } from './available-rooms.component';

describe('AvailableRoomsComponent', () => {
  let component: AvailableRoomsComponent;
  let fixture: ComponentFixture<AvailableRoomsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AvailableRoomsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AvailableRoomsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
