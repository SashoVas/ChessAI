import { TestBed } from '@angular/core/testing';

import { WebSocketsServiceService } from './web-sockets-service.service';

describe('WebSocketsServiceService', () => {
  let service: WebSocketsServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WebSocketsServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
