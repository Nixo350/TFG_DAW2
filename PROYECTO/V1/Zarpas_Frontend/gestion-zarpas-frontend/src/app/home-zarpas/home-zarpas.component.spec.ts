import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeZarpasComponent } from './home-zarpas.component';

describe('HomeZarpasComponent', () => {
  let component: HomeZarpasComponent;
  let fixture: ComponentFixture<HomeZarpasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeZarpasComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomeZarpasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
