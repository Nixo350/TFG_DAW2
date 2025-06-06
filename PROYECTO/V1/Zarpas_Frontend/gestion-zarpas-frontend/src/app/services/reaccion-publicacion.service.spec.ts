import { TestBed } from '@angular/core/testing';

import { ReaccionPublicacionService } from './reaccion-publicacion.service';

describe('ReaccionPublicacionService', () => {
  let service: ReaccionPublicacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReaccionPublicacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
