import { HttpClient } from '@angular/common/http';
import { Injectable  } from '@angular/core';
import {Observable, tap } from 'rxjs';
import { Auth } from '../models/auth';
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private httpClient:HttpClient) { }

  public isLoggedIn():boolean{
    const token = localStorage.getItem('authToken');
    return token!=null;
  }

  public login(credentials:{ username: string; password: string }):Observable<Auth>{
    return this.httpClient.post<Auth>('http://localhost:8080/login',credentials).pipe(
        tap(user => {
          console.log(user)
          if (user.authToken) {
            localStorage.setItem('authToken', user.authToken);
          }
        })
      );

  }

  public getToken(){
    return localStorage.getItem('authToken');
  }
  public logout() {
    localStorage.removeItem('authToken');
  }
}
