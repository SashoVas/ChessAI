import { TestBed } from '@angular/core/testing';

import { ChessBoardServiceService } from './chess-board-service.service';

describe('ChessBoardServiceService', () => {
  let service: ChessBoardServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ChessBoardServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
