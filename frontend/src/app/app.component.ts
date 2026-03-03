import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent],
  template: `
    <!-- Navbar is shown on every page -->
    <app-navbar></app-navbar>

    <!-- router-outlet loads the routed component (Feed, Profile, Login, etc.) -->
    <router-outlet></router-outlet>
  `,
  styles: []
})
export class AppComponent {

  // Application title (not used in template currently)
  title = 'RevConnect';
}