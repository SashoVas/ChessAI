declare const global: any;

import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ChessBoardServiceService } from '../../services/chess-board-service.service';
import { WebSocketsServiceService } from '../../services/web-sockets-service.service';
import { Subscription } from 'rxjs';
import { RoomServiceService } from '../../services/room-service.service';

@Component({
  selector: 'app-chess-board',
    templateUrl: './chess-board.component.html',
  styleUrl: './chess-board.component.css',
  standalone: true,
  imports: [  CommonModule,  
    FormsModule],

})
export class ChessBoardComponent {

  @ViewChild('chessboard',{static:true}) chessBoardEl?:ElementRef<HTMLDivElement>
 // two-way bindings
  fen = 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1';
  private roomSubscription!: Subscription;

  // board state
  initialPosition: string[][] = Array(8).fill(null).map(() => Array(8).fill(''));
  possibleMoves: string[] = [];
  currentRoomId = '';
  currentColor = '';

  // drag/drop
  private draggedPiece: HTMLElement | null = null;
  private offsetX = 0;
  private offsetY = 0;
  private from = 0;
  private to = 0;

  // config
  isBotMode = true;
  readonly squareSize = 60;

  constructor(
    private chessService: ChessBoardServiceService,
    private roomService:RoomServiceService,
    private webSocketService:WebSocketsServiceService) {
  }

  ngAfterViewInit():void{
        this.loadFen(this.fen);
        document
          .getElementById('chessboard')!
          .addEventListener('mousedown', (e) => this.handleMouseDown(e));
  }
  
  ngOnInit(): void {
    this.webSocketService.activate();
  }

  loadFen(fen: string): void {
    this.initialPosition=this.chessService.fenToBoard(fen);
    if(this.chessBoardEl?.nativeElement)
      this.chessService.createChessboard(this.chessBoardEl?.nativeElement,this.initialPosition,this.currentColor);
  }

  loadFenButtonClick(): void {
    this.loadFen(this.fen);
  }

  joinRoomOnClick(): void {
    this.roomSubscription=this.webSocketService.joinRoom(this.currentRoomId).subscribe({
      next: (msg) => this.parseMessage(msg),
      error: (err) => console.error('WebSocket error:', err)
    });
  }

  private parseMessage(msg: any):void{
      this.possibleMoves = msg.nextMoves;
      this.fen = msg.fen;
      this.loadFen(this.fen);
      console.log('Received', msg);
  }


  createRoom(): void {
    this.roomService.createRoom(this.isBotMode)
      .then(json => {
        this.currentRoomId = json.gameId;
        this.currentColor = json.user1Color;
        this.roomSubscription=this.webSocketService.joinRoom(this.currentRoomId).subscribe({
          next: (msg) => this.parseMessage(msg),
          error: (err) => console.error('WebSocket error:', err)
        });
      })
      .catch(console.error);
  }

  private handleMouseDown(e: MouseEvent): void {
    const target = e.target as HTMLElement;
    if (!target.classList.contains('piece')) return;

    this.draggedPiece = target;
    const parentSquare = target.parentNode as HTMLElement;
    this.from = Array.prototype.indexOf.call(
      parentSquare.parentNode!.children,
      parentSquare
    );

    const rect = target.getBoundingClientRect();
    const boardRect = this.chessBoardEl!.nativeElement.getBoundingClientRect();
    this.offsetX = e.clientX - rect.left;
    this.offsetY = e.clientY - rect.top;

    target.style.position = 'absolute';
    target.style.width = `${this.squareSize}px`;
    target.style.height = `${this.squareSize}px`;
    target.style.cursor = 'grabbing';
    this.chessBoardEl!.nativeElement.appendChild(target);

    const x = e.clientX - boardRect.left - this.offsetX;
    const y = e.clientY - boardRect.top - this.offsetY;
    target.style.left = `${x}px`;
    target.style.top = `${y}px`;

    document.addEventListener('mousemove', this.handleMouseMove);
    document.addEventListener('mouseup', this.handleMouseUp);

    this.chessService.getAttackedPositions(this.from,this.currentColor,this.possibleMoves).forEach(pos => this.chessService.highlightSquare(this.chessBoardEl!.nativeElement,pos));
  }

  private handleMouseMove = (e: MouseEvent): void => {
    if (!this.draggedPiece) return;
    const board = this.chessBoardEl!.nativeElement;
    const rect = board.getBoundingClientRect();
    let x = e.clientX - rect.left - this.offsetX;
    let y = e.clientY - rect.top - this.offsetY;
    x = Math.max(0, Math.min(x, board.offsetWidth - this.squareSize));
    y = Math.max(0, Math.min(y, board.offsetHeight - this.squareSize));
    this.draggedPiece.style.left = `${x}px`;
    this.draggedPiece.style.top = `${y}px`;
  }

  private handleMouseUp = (e: MouseEvent): void => {
    if (!this.draggedPiece) return;
    const board = this.chessBoardEl!.nativeElement;
    const rect = board.getBoundingClientRect();
    const col = Math.floor((e.clientX - rect.left) / this.squareSize);
    const row = Math.floor((e.clientY - rect.top) / this.squareSize);
    this.to = row * 8 + col;

    const fromAlg = this.chessService.toChessAlgebra(this.from, this.currentColor === 'BLACK');
    const toAlg = this.chessService.toChessAlgebra(this.to, this.currentColor === 'BLACK');
    const valid = this.possibleMoves.includes(fromAlg + toAlg);
    const targetSq = board.children[this.to] as HTMLElement;

    // reset dragged styling
    Object.assign(this.draggedPiece.style, {
      position: '',
      width: '',
      height: '',
      left: '',
      top: '',
      cursor: 'grab'
    });

    if (valid) {
      if (targetSq.hasChildNodes()) targetSq.removeChild(targetSq.lastChild!);
      targetSq.appendChild(this.draggedPiece);
      this.webSocketService.sendMessage(fromAlg + toAlg, this.currentRoomId,this.isBotMode);
    } else {
      board.children[this.from].appendChild(this.draggedPiece);
    }

    document.removeEventListener('mousemove', this.handleMouseMove);
    document.removeEventListener('mouseup', this.handleMouseUp);
    this.chessService.getAttackedPositions(this.from,this.currentColor,this.possibleMoves).forEach(pos => this.chessService.unhighlightSquare(this.chessBoardEl!.nativeElement,pos));
    this.draggedPiece = null;
  }

  ngOnDestroy(): void {
    this.roomSubscription?.unsubscribe();
  }

}
