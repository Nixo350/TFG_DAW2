import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditPublicacionDialogComponent } from './edit-publicacion-dialog.component';

describe('EditPublicacionDialogComponent', () => {
  let component: EditPublicacionDialogComponent;
  let fixture: ComponentFixture<EditPublicacionDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditPublicacionDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditPublicacionDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
