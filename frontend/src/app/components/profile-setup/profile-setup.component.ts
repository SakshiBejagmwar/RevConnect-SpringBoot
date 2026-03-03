import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CreatorService, CreatorProfile } from '../../services/creator.service';
import { BusinessService, BusinessProfile } from '../../services/business.service';

@Component({
  selector: 'app-profile-setup',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile-setup.component.html',
  styleUrls: ['./profile-setup.component.css']
})
export class ProfileSetupComponent implements OnInit {
  // Stores current user's role (CREATOR / BUSINESS)
  userRole: string = '';

  // UI state flags/messages
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  // Model bound to Creator Profile form fields
  creatorProfile: CreatorProfile = {
    creatorName: '',
    category: '',
    detailedBio: '',
    contactInfo: '',
    website: '',
    socialMediaLinks: '',
    externalLinks: ''
  };

  // Model bound to Business Profile form fields
  businessProfile: BusinessProfile = {
    businessName: '',
    industry: '',
    detailedBio: '',
    contactInfo: '',
    website: '',
    socialMediaLinks: '',
    businessAddress: '',
    businessHours: '',
    externalLinks: '',
    productsDescription: ''
  };

  constructor(
    private authService: AuthService,         // Used to get current logged-in user + role
    private creatorService: CreatorService,   // API calls for creator profile
    private businessService: BusinessService, // API calls for business profile
    private router: Router                    // Used for redirects/navigation
  ) {}

  // Runs when component loads
  ngOnInit(): void {
    // Get the currently logged-in user details
    const currentUser = this.authService.getCurrentUser();

    if (currentUser) {
      // Set role so UI can show correct form (Creator or Business)
      this.userRole = currentUser.role;

      // If user already has a profile saved, load it for editing
      if (this.userRole === 'CREATOR') {
        this.loadCreatorProfile();
      } else if (this.userRole === 'BUSINESS') {
        this.loadBusinessProfile();
      }
    } else {
      // If user is not logged in, redirect to login page
      this.router.navigate(['/login']);
    }
  }

  // Loads logged-in creator's existing profile (if it exists)
  loadCreatorProfile(): void {
    this.creatorService.getMyProfile().subscribe({
      next: (profile) => {
        this.creatorProfile = profile; // Fill form with existing data
      },
      error: () => {
        // Profile doesn't exist yet, that's okay (new profile setup)
      }
    });
  }

  // Loads logged-in business's existing profile (if it exists)
  loadBusinessProfile(): void {
    this.businessService.getMyProfile().subscribe({
      next: (profile) => {
        this.businessProfile = profile; // Fill form with existing data
      },
      error: () => {
        // Profile doesn't exist yet, that's okay (new profile setup)
      }
    });
  }

  // Submit creator profile form (create or update)
  onSubmitCreator(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.creatorService.createOrUpdateProfile(this.creatorProfile).subscribe({
      next: () => {
        this.successMessage = 'Creator profile saved successfully!';
        this.isLoading = false;

        // Navigate after a small delay to show success message
        setTimeout(() => {
          this.router.navigate(['/profile']);
        }, 1500);
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Failed to save profile';
        this.isLoading = false;
      }
    });
  }

  // Submit business profile form (create or update)
  onSubmitBusiness(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.businessService.createOrUpdateProfile(this.businessProfile).subscribe({
      next: () => {
        this.successMessage = 'Business profile saved successfully!';
        this.isLoading = false;

        // Navigate after a small delay to show success message
        setTimeout(() => {
          this.router.navigate(['/profile']);
        }, 1500);
      },
      error: (error) => {
        this.errorMessage = error.error?.message || 'Failed to save profile';
        this.isLoading = false;
      }
    });
  }

  // Skip profile setup and go directly to feed
  skipSetup(): void {
    this.router.navigate(['/feed']);
  }
}