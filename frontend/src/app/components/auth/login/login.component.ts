/*
Takes user input (email/username + password)
Validates input
Sends login request to backend
On success → navigates to /feed
On failure → shows error message
Handles loading state during API cal
*/


import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { LoginRequest } from '../../../models/user.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  // Stores user login credentials (bound to form inputs)
  credentials: LoginRequest = {
    emailOrUsername: '',
    password: ''
  };

  // Stores error message to show in UI
  errorMessage = '';

  // Controls loading spinner / button disable state
  isLoading = false;

  constructor(
    private authService: AuthService, // Handles authentication API calls
    private router: Router            // Used for navigation after login
  ) {}

  // Runs when login form is submitted
  onSubmit(): void {

    // Clear any previous error messages
    this.errorMessage = '';

    // Validate form fields before sending request
    if (!this.credentials.emailOrUsername || !this.credentials.password) {
      this.errorMessage = 'Please fill in all fields';
      return; // Stop execution if validation fails
    }

    // Show loading state while API call is in progress
    this.isLoading = true;

    // Call login API via AuthService
    this.authService.login(this.credentials).subscribe({

      // If login is successful
      next: () => {
        // Navigate to feed/dashboard page
        this.router.navigate(['/feed']);
      },

      // If login fails
      error: (error) => {
        console.error('Login error:', error);

        // Display backend error message if available
        if (error.error && error.error.message) {
          this.errorMessage = error.error.message;
        } 
        // Otherwise show generic error
        else if (error.message) {
          this.errorMessage = error.message;
        } 
        else {
          this.errorMessage = 'Login failed. Please check your credentials.';
        }

        // Stop loading indicator
        this.isLoading = false;
      }
    });
  }
}