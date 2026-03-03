\# RevConnect - Social Media Platform



\## Description



RevConnect is a comprehensive social media platform that connects three types of users: Personal Users, Content Creators, and Businesses. Built with modern web technologies, it provides a seamless experience for sharing content, building networks, and engaging with communities.



\### Key Features

\- \*\*Multi-User Types\*\*: Personal, Creator, and Business profiles with tailored features

\- \*\*Content Management\*\*: Create, edit, delete posts with media support (images, videos, documents)

\- \*\*Social Interactions\*\*: Like, comment, share posts with real-time engagement

\- \*\*Networking\*\*: Follow creators and send connection requests to other users

\- \*\*Analytics\*\*: Comprehensive post analytics with engagement metrics

\- \*\*File Upload\*\*: Support for images, videos, and documents up to 10MB

\- \*\*Real-time Notifications\*\*: Get notified about likes, comments, follows, and connections

\- \*\*Advanced Search\*\*: Search users by name/username and posts by hashtags

\- \*\*Responsive Design\*\*: Mobile-first design that works on all devices

\- \*\*Secure Authentication\*\*: JWT-based authentication with password encryption



\## Table of Contents



1\. \[Installation](#installation)

2\. \[Usage](#usage)

3\. \[Configuration](#configuration)

4\. \[Project Structure](#project-structure)

5\. \[Technologies Used](#technologies-used)

6\. \[Features](#features)



\## Installation



\### Prerequisites



Before running the application, ensure you have the following installed:



\- \*\*Node.js\*\* (v18 or higher)

\- \*\*Java JDK 17\*\*

\- \*\*MySQL 8.0\*\*

\- \*\*Maven\*\* (or use included Maven wrapper)

\- \*\*Git\*\*



\### Database Setup



1\. \*\*Start MySQL Server\*\*

2\. \*\*Create Database\*\*:

&nbsp;  ```sql

&nbsp;  CREATE DATABASE revconnect\_db;

&nbsp;  ```

3\. \*\*Database Credentials\*\* (default):

&nbsp;  - Host: `localhost:3306`

&nbsp;  - Database: `revconnect\_db`

&nbsp;  - Username: `root`

&nbsp;  - Password: `admin@123`



\### Backend Setup



1\. \*\*Clone the repository\*\*:

&nbsp;  ```bash

&nbsp;  git clone <repository-url>

&nbsp;  cd revconnect/backend

&nbsp;  ```



2\. \*\*Navigate to backend directory\*\*:

&nbsp;  ```bash

&nbsp;  cd revconnect

&nbsp;  ```



3\. \*\*Install dependencies and run\*\*:

&nbsp;  ```bash

&nbsp;  ./mvnw spring-boot:run

&nbsp;  ```

&nbsp;  Or on Windows:

&nbsp;  ```bash

&nbsp;  mvnw.cmd spring-boot:run

&nbsp;  ```



4\. \*\*Verify backend is running\*\*:

&nbsp;  - API: http://localhost:8080

&nbsp;  - Swagger UI: http://localhost:8080/swagger-ui.html



\### Frontend Setup



1\. \*\*Navigate to frontend directory\*\*:

&nbsp;  ```bash

&nbsp;  cd revconnect-frontend

&nbsp;  ```



2\. \*\*Install dependencies\*\*:

&nbsp;  ```bash

&nbsp;  npm install

&nbsp;  ```



3\. \*\*Start development server\*\*:

&nbsp;  ```bash

&nbsp;  npm start

&nbsp;  ```

&nbsp;  Or:

&nbsp;  ```bash

&nbsp;  ng serve

&nbsp;  ```



4\. \*\*Access the application\*\*:

&nbsp;  - Frontend: http://localhost:4200



\## Usage



\### Getting Started



1\. \*\*Register a new account\*\*:

&nbsp;  - Visit http://localhost:4200/register

&nbsp;  - Choose your user type (Personal, Creator, or Business)

&nbsp;  - Fill in required information

&nbsp;  - Click "Register"



2\. \*\*Login\*\*:

&nbsp;  - Visit http://localhost:4200/login

&nbsp;  - Enter your email/username and password

&nbsp;  - Click "Login"



3\. \*\*Complete your profile\*\*:

&nbsp;  - Add bio, location, website, and profile picture

&nbsp;  - For Creators: Add portfolio, skills, and achievements

&nbsp;  - For Businesses: Add company details and products



\### Core Features



\#### Creating Posts

\- Click on the post creation area in the feed

\- Type your content

\- Optionally upload images, videos, or documents

\- Add hashtags for better discoverability

\- Click "Post" to publish



\#### Social Interactions

\- \*\*Like Posts\*\*: Click the heart icon

\- \*\*Comment\*\*: Click comment icon and type your message

\- \*\*Share\*\*: Click share icon to repost content

\- \*\*Pin Posts\*\*: Pin important posts to your profile



\#### Networking

\- \*\*Follow Creators\*\*: Click "Follow" on creator profiles

\- \*\*Send Connections\*\*: Send connection requests to other users

\- \*\*Accept/Reject\*\*: Manage incoming connection requests



\#### Analytics (For your posts)

\- View likes, comments, shares, and reach

\- Calculate engagement rate

\- Track post performance over time



\### User Types



\#### Personal Users

\- Create and share personal content

\- Connect with friends and colleagues

\- Follow content creators

\- Engage with posts through likes and comments



\#### Content Creators

\- Showcase portfolio and skills

\- Build follower base

\- Share creative content

\- Track engagement analytics



\#### Businesses

\- Promote products and services

\- Create business posts with call-to-action buttons

\- Connect with potential customers

\- Analyze post performance for marketing insights



\## Configuration



\### Backend Configuration



\*\*File\*\*: `src/main/resources/application.properties`



```properties

\# Database Configuration

spring.datasource.url=jdbc:mysql://localhost:3306/revconnect\_db

spring.datasource.username=root

spring.datasource.password=admin@123



\# Server Configuration

server.port=8080



\# File Upload Configuration

spring.servlet.multipart.max-file-size=10MB

spring.servlet.multipart.max-request-size=10MB



\# JWT Configuration (in code)

jwt.secret=revconnect-secret-key-for-jwt-token-generation-2026-secure

jwt.expiration=86400000  # 24 hours

```



\### Frontend Configuration



\*\*File\*\*: `src/environments/environment.ts`



```typescript

export const environment = {

&nbsp; production: false,

&nbsp; apiUrl: 'http://localhost:8080/api'

};

```



\### CORS Configuration



The backend is configured to allow requests from:

\- http://localhost:4200 (Angular dev server)

\- http://localhost:3000 (Alternative frontend)

\- http://localhost:8080 (Backend itself)



\## Project Structure



```

revconnect/

│

├── backend/                          # Spring Boot Backend

│   ├── src/main/java/com/revconnect/

│   │   ├── config/                   # Configuration classes

│   │   │   ├── CorsConfig.java

│   │   │   ├── SecurityConfig.java

│   │   │   ├── OpenApiConfig.java

│   │   │   └── ...

│   │   ├── controller/               # REST API Controllers

│   │   │   ├── AuthController.java

│   │   │   ├── PostController.java

│   │   │   ├── UserController.java

│   │   │   └── ... (16 controllers)

│   │   ├── entity/                   # JPA Entities

│   │   │   ├── User.java

│   │   │   ├── Post.java

│   │   │   ├── Comment.java

│   │   │   └── ... (15 entities)

│   │   ├── repository/               # Data Access Layer

│   │   │   ├── UserRepository.java

│   │   │   ├── PostRepository.java

│   │   │   └── ... (15 repositories)

│   │   ├── service/                  # Business Logic

│   │   │   ├── PostService.java

│   │   │   ├── UserService.java

│   │   │   └── ... (15 services)

│   │   ├── security/                 # JWT Security

│   │   │   ├── JwtUtil.java

│   │   │   └── JwtAuthFilter.java

│   │   └── exception/                # Error Handling

│   │       └── GlobalExceptionHandler.java

│   ├── src/main/resources/

│   │   ├── application.properties

│   │   └── static/

│   └── pom.xml

│

├── frontend/                         # Angular Frontend

│   ├── src/app/

│   │   ├── components/               # UI Components

│   │   │   ├── auth/

│   │   │   │   ├── login/

│   │   │   │   └── register/

│   │   │   ├── feed/

│   │   │   ├── post-card/

│   │   │   ├── profile/

│   │   │   ├── navbar/

│   │   │   ├── notifications/

│   │   │   ├── search/

│   │   │   ├── connections/

│   │   │   └── analytics/

│   │   ├── services/                 # HTTP Services

│   │   │   ├── auth.service.ts

│   │   │   ├── post.service.ts

│   │   │   ├── user.service.ts

│   │   │   └── ... (11 services)

│   │   ├── models/                   # TypeScript Interfaces

│   │   │   ├── user.model.ts

│   │   │   ├── post.model.ts

│   │   │   └── connection.model.ts

│   │   ├── guards/                   # Route Guards

│   │   │   └── auth.guard.ts

│   │   ├── interceptors/             # HTTP Interceptors

│   │   │   └── auth.interceptor.ts

│   │   ├── app.component.ts

│   │   ├── app.routes.ts

│   │   └── app.config.ts

│   ├── src/environments/

│   │   └── environment.ts

│   ├── angular.json

│   ├── package.json

│   └── tsconfig.json

│

├── uploads/                          # File Upload Directory

├── README.md

└── documentation/

```



\## Technologies Used



\### Backend Technologies

\- \*\*Java 17\*\* - Programming language

\- \*\*Spring Boot 3.x\*\* - Application framework

\- \*\*Spring Security\*\* - Authentication and authorization

\- \*\*Spring Data JPA\*\* - Data persistence

\- \*\*Hibernate\*\* - ORM framework

\- \*\*MySQL 8.0\*\* - Database

\- \*\*Maven\*\* - Build tool

\- \*\*JWT (JSON Web Tokens)\*\* - Authentication

\- \*\*BCrypt\*\* - Password hashing

\- \*\*Swagger/OpenAPI 3\*\* - API documentation

\- \*\*Jackson\*\* - JSON processing



\### Frontend Technologies

\- \*\*Angular 15+\*\* - Frontend framework

\- \*\*TypeScript\*\* - Programming language

\- \*\*RxJS\*\* - Reactive programming

\- \*\*Angular Router\*\* - Navigation

\- \*\*Angular Forms\*\* - Form handling

\- \*\*HttpClient\*\* - HTTP requests

\- \*\*CSS3\*\* - Styling

\- \*\*HTML5\*\* - Markup

\- \*\*Node.js\*\* - Runtime environment

\- \*\*npm\*\* - Package manager



\### Development Tools

\- \*\*VS Code\*\* - Frontend IDE

\- \*\*Eclipse\*\* - Backend IDE

\- \*\*Postman\*\* - API testing

\- \*\*MySQL Workbench\*\* - Database management

\- \*\*Git\*\* - Version control

\- \*\*Chrome DevTools\*\* - Debugging



\### Architecture

\- \*\*Monolithic Architecture\*\* - Single backend application

\- \*\*RESTful API\*\* - HTTP-based API design

\- \*\*Single Page Application (SPA)\*\* - Frontend architecture

\- \*\*JWT Authentication\*\* - Stateless authentication

\- \*\*Responsive Design\*\* - Mobile-first approach





\### Swagger UI

Access comprehensive API documentation at: http://localhost:8080/swagger-ui.html



\### Key API Endpoints



\#### Authentication

\- `POST /api/auth/register` - User registration

\- `POST /api/auth/login` - User login



\#### Posts

\- `GET /api/posts/feed` - Get user feed

\- `POST /api/posts` - Create new post

\- `PUT /api/posts/{id}` - Update post

\- `DELETE /api/posts/{id}` - Delete post

\- `POST /api/posts/share/{id}` - Share post



\#### Social Interactions

\- `POST /api/likes/{postId}` - Like post

\- `DELETE /api/likes/{postId}` - Unlike post

\- `POST /api/comments` - Add comment

\- `DELETE /api/comments/{id}` - Delete comment



\#### User Management

\- `GET /api/users/{id}` - Get user profile

\- `PUT /api/users/{id}` - Update profile

\- `GET /api/search/users` - Search users



\#### Connections \& Follows

\- `POST /api/connections/send/{userId}` - Send connection request

\- `POST /api/connections/accept/{requestId}` - Accept connection

\- `POST /api/follows/{userId}` - Follow user

\- `DELETE /api/follows/{userId}` - Unfollow user



\#### Analytics

\- `GET /api/analytics/post/{postId}` - Get post analytics

\- `GET /api/analytics/post/{postId}/engagement` - Get engagement rate



\#### File Upload

\- `POST /api/files/upload` - Upload file

\- `DELETE /api/files/delete` - Delete file



\### Authentication

All protected endpoints require JWT token in Authorization header:

```

Authorization: Bearer <jwt-token>

```



\## Features



\### ✅ Implemented Features



\#### User Management

\- User registration with email verification

\- Secure login with JWT authentication

\- Profile management (bio, location, website, profile picture)

\- Three user types: Personal, Creator, Business

\- Account privacy settings (Public/Private)



\#### Content Management

\- Create, edit, delete posts

\- Rich text content with hashtag support

\- Media upload (images, videos, documents up to 10MB)

\- Post types: Regular, Promotional, Announcement

\- Pin important posts to profile

\- Scheduled posts (future publishing)



\#### Social Features

\- Like and unlike posts

\- Comment on posts with nested replies

\- Share/repost content

\- Follow content creators

\- Send and manage connection requests

\- Real-time notifications for all interactions



\#### Analytics \& Insights

\- Post performance metrics (likes, comments, shares, reach)

\- Engagement rate calculation

\- Analytics dashboard for content creators and businesses

\- Trending posts and hashtags



\#### Search \& Discovery

\- Search users by name or username

\- Search posts by hashtags

\- Filter feed by post type or user type

\- Trending content discovery



\#### Security \& Privacy

\- JWT-based authentication

\- Password encryption with BCrypt

\- Route protection with guards

\- CORS configuration

\- Input validation and sanitization



\#### Responsive Design

\- Mobile-first responsive design

\- Optimized for desktop, tablet, and mobile

\- Touch-friendly interface

\- Progressive Web App features



\### 🚀 Advanced Features



\#### File Management

\- Secure file upload with validation

\- Multiple file format support

\- File size optimization

\- Automatic file cleanup



\#### Real-time Features

\- Live notification updates

\- Real-time engagement counters

\- Instant feed updates



\#### Performance Optimization

\- Lazy loading for components

\- Image optimization

\- Efficient database queries

\- Caching strategies





\*\*Live Demo\*\*: \[Coming Soon]

\*\*Documentation\*\*: \[API Docs](http://localhost:8080/swagger-ui.html)

\*\*Version\*\*: 1.0.0

