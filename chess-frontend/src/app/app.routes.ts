import { Routes } from '@angular/router';
import { ChessBoardComponent } from './components/chess-board/chess-board.component';
import { HomePageComponent } from './components/home-page/home-page.component';
import { ProfileComponent } from './components/profile/profile.component';
import { GameHistoryComponent } from './components/game-history/game-history.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ErrorPageComponent } from './components/error-page/error-page.component';
import { authGuard } from './guards/auth.guard';
import { AvailableRoomsComponent } from './components/available-rooms/available-rooms.component';

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
        path:"profile",
        component: ProfileComponent,
        title: "Profile",
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
    }
];
