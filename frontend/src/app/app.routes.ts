import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./components/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./components/auth/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'profile-setup',
    loadComponent: () => import('./components/profile-setup/profile-setup.component').then(m => m.ProfileSetupComponent),
    canActivate: [authGuard]
  },
  {
    path: 'feed',
    loadComponent: () => import('./components/feed/feed.component').then(m => m.FeedComponent),
    canActivate: [authGuard]
  },
  {
    path: 'profile/:id',
    loadComponent: () => import('./components/profile/profile.component').then(m => m.ProfileComponent),
    canActivate: [authGuard]
  },
  {
    path: 'connections',
    loadComponent: () => import('./components/connections/connections.component').then(m => m.ConnectionsComponent),
    canActivate: [authGuard]
  },
  {
    path: 'notifications',
    loadComponent: () => import('./components/notifications/notifications.component').then(m => m.NotificationsComponent),
    canActivate: [authGuard]
  },
  {
    path: 'search',
    loadComponent: () => import('./components/search/search.component').then(m => m.SearchComponent),
    canActivate: [authGuard]
  },
  {
    path: 'analytics',
    loadComponent: () => import('./components/analytics/analytics.component').then(m => m.AnalyticsComponent),
    canActivate: [authGuard]
  },
  {
    path: '**',
    redirectTo: '/login'
  }
];

//This file defines the navigation structure and route protection of the entire Angular application.
