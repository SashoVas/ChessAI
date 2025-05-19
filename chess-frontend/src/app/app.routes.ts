import { Routes } from '@angular/router';
import { ChessBoardComponent } from './components/chess-board/chess-board.component';
import { HomePageComponent } from './components/home-page/home-page.component';
import { ProfileComponent } from './components/profile/profile.component';
import { GameHistoryComponent } from './components/game-history/game-history.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';

export const routes: Routes = [
    {
        path: '',
        component: HomePageComponent,
        title: 'Chess Home Page'
    },
    {
        path:'game',
        component: ChessBoardComponent,
        title: 'Chess Game'
    },
    {
        path:"profile",
        component: ProfileComponent,
        title: "Profile"
    },
    {
        path:"gameHistory",
        component: GameHistoryComponent,
        title:"Game History"
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
    }
];
