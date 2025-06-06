import { TestBed } from '@angular/core/testing';

import { ComentarioReaccionService } from './comentario-reaccion.service';

describe('ComentarioReaccionService', () => {
  let service: ComentarioReaccionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ComentarioReaccionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
