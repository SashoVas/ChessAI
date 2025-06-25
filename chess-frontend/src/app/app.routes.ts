import { Routes } from '@angular/router';
import { ChessBoardComponent } from './components/chess-board/chess-board.component';
import { HomePageComponent } from './components/home-page/home-page.component';
import { GameHistoryComponent } from './components/game-history/game-history.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ErrorPageComponent } from './components/error-page/error-page.component';
import { authGuard } from './guards/auth.guard';
import { AvailableRoomsComponent } from './components/available-rooms/available-rooms.component';
import { CreateGameComponent } from './components/create-game/create-game.component';

export const routes: Routes = [
    {
        path: '',
        component: HomePageComponent,
        title: 'Chess Home Page'
    },
    {
        path:'game/:roomId',
        component: ChessBoardComponent,
        title: 'Chess Game',
        canActivate: [authGuard]
    },
    {
        path:"gameHistory",
        component: GameHistoryComponent,
        title:"Game History",
        canActivate: [authGuard]
    },
    {
        path:"availableRooms",
        component: AvailableRoomsComponent,
        title:"Available Rooms",
        canActivate: [authGuard]
    },
    {
        path:"login",
        component: LoginComponent,
        title:"Login"
    },
    {
        path:"register",
        component: RegisterComponent,
        title:"Register"
    },
    {
        path:"error",
        component: ErrorPageComponent,
        title:"Error"
    },
    {
        path: "create-game",
        component: CreateGameComponent,
        title: "Create Game",
        canActivate: [authGuard]
    },
    {
        path:"**",
        component: ErrorPageComponent,
        title:"Error"
    },
];
