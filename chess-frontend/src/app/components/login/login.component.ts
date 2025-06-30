import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterModule,CommonModule,FormsModule,ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  isLoading = false;
  errorMessage = '';

  loginForm = this.fb.group({
    username: ['', [Validators.required]],
    password: ['', Validators.required],
    rememberMe: [false]
  });
  
    constructor(
    private fb: FormBuilder,
    private authService:AuthService,
    private router: Router
  ) {}

  onSubmit() {
    if (this.loginForm.invalid) return;
    this.isLoading = true;
    this.errorMessage = '';

    const credentials = {
      username: this.loginForm.value.username!,
      password: this.loginForm.value.password!
    };
    this.authService.login(credentials).subscribe({
    next: value => this.router.navigate(['/']),
    error: err => {
      this.isLoading = false;
      this.errorMessage = err instanceof Error ? err.message : 'Invalid Credentials. Please try again.';
    }
  });
  }
}
