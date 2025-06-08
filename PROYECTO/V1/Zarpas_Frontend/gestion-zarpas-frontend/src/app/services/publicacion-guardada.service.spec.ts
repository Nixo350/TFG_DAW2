import { TestBed } from '@angular/core/testing';

import { PublicacionGuardadaService } from './publicacion-guardada.service';

describe('PublicacionGuardadaService', () => {
  let service: PublicacionGuardadaService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PublicacionGuardadaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
