import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterOutlet } from '@angular/router';
import { ChessBoardComponent } from './components/chess-board/chess-board.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [FormsModule,ChessBoardComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'chess-frontend';
}
