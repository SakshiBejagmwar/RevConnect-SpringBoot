/*
Collects user registration details
Validates input fields (password, email, username)
Calls backend register API
Redirects based on user role
Shows loading and error states
*/



import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { User } from '../../../models/user.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  // Object bound to registration form inputs
  user: User = {
    name: '',
    username: '',
    email: '',
    password: '',
    role: 'USER',              // Default role
    accountPrivacy: 'PUBLIC'   // Default privacy setting
  };

  // Used to confirm password field
  confirmPassword = '';

  // Stores error message to show in UI
  errorMessage = '';

  // Controls loading spinner / button disable state
  isLoading = false;

  constructor(
    private authService: AuthService, // Handles API calls for authentication
    private router: Router            // Used for navigation after registration
  ) {}

  // Runs when registration form is submitted
  onSubmit(): void {

    // Clear previous error message
    this.errorMessage = '';

    // -----------------------
    // Basic Field Validation
    // -----------------------

    if (!this.user.name || !this.user.username || !this.user.email || !this.user.password) {
      this.errorMessage = 'Please fill in all required fields';
      return;
    }

    // Check if passwords match
    if (this.user.password !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match';
      return;
    }

    // Minimum password length validation
    if (this.user.password.length < 6) {
      this.errorMessage = 'Password must be at least 6 characters';
      return;
    }

    // -----------------------
    // Email Format Validation
    // -----------------------

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.user.email)) {
      this.errorMessage = 'Please enter a valid email address';
      return;
    }

    // -----------------------
    // Username Validation
    // (3-20 characters, letters/numbers/underscore only)
    // -----------------------

    const usernameRegex = /^[a-zA-Z0-9_]{3,20}$/;
    if (!usernameRegex.test(this.user.username)) {
      this.errorMessage = 'Username must be 3-20 characters (letters, numbers, underscore only)';
      return;
    }

    // Show loading state while API call is processing
    this.isLoading = true;

    // Call backend register API
    this.authService.register(this.user).subscribe({

      // If registration is successful
      next: () => {

        // Redirect based on role
        if (this.user.role === 'CREATOR' || this.user.role === 'BUSINESS') {
          this.router.navigate(['/profile-setup']);
        } else {
          this.router.navigate(['/feed']);
        }
      },

      // If registration fails
      error: (error) => {
        console.error('Registration error:', error);

        if (error.error && error.error.message) {
          this.errorMessage = error.error.message;
        } else if (error.message) {
          this.errorMessage = error.message;
        } else {
          this.errorMessage = 'Registration failed. Please try again.';
        }

        // Stop loading indicator
        this.isLoading = false;
      }
    });
  }
}