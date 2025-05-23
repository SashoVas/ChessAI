import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { CreateGameComponent } from '../create-game/create-game.component';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [CommonModule,CreateGameComponent],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css'
})
export class HomePageComponent {

  timeControls = [
    { name: 'UltraBullet', time: '½ + 0' },
    { name: 'Bullet', time: '1 + 0' },
    { name: 'Blitz', time: '3 + 2' },
    { name: 'Rapid', time: '10 + 0' },
    { name: 'Classical', time: '30 + 20' }
  ];

  hoverEffect(timeControl: any) {
   }

  startAIGame() {
   }

  startCustomGame() {
   }
}
