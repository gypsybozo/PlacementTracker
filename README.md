# Placement Tracker

A comprehensive web application for students preparing for technical placements. Track your coding progress, collaborate with peers, and get notified about relevant job opportunities.

## Features

- User Registration & Authentication
- Progress Tracking
- Collaboration with Study Groups
- Company Notification System
- Admin Panel for User & Job Management

## Tech Stack

- Java 11
- Spring Boot 2.7.x
- Spring MVC
- Spring Security
- Spring Data JPA
- Thymeleaf
- PostgreSQL
- Maven

## Setup Instructions

### Prerequisites

- JDK 11 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher

### Database Setup

1. Create a PostgreSQL database:

```sql
CREATE DATABASE placement_tracker;
```

2. Update database credentials in `src/main/resources/application.properties` if needed:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/placement_tracker
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### Email Configuration

Update email configuration in `src/main/resources/application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

For Gmail, you need to generate an "App Password" instead of using your regular password.

### Running the Application

1. Clone the repository:

```bash
git clone <repository-url>
cd coding-placement-tracker
```

2. Build the project:

```bash
mvn clean install
```

3. Run the application:

```bash
mvn spring-boot:run
```

4. Access the application at [http://localhost:8080](http://localhost:8080)

## Sample Credentials

When the application starts for the first time, the database schema will be created. You need to register a new user.

### Creating an Admin User

You can create a regular user through the registration form and then update the role in the database:

```sql
UPDATE users_roles SET role = 'ROLE_ADMIN' WHERE user_id = 1;
```

## Design Patterns Used

1. **Singleton Pattern** - Applied via Spring's default bean scoping
2. **Factory Pattern** - Used for creating user-related objects
3. **Observer Pattern** - Implemented in the notification system
4. **Strategy Pattern** - Used for different notification methods
5. **Repository Pattern** - Used for data access abstraction

## SOLID Principles Applied

1. **Single Responsibility Principle** - Each class has one responsibility
2. **Open/Closed Principle** - Extended functionality without modifying existing code
3. **Liskov Substitution Principle** - Interface implementations are substitutable
4. **Interface Segregation Principle** - Specific interfaces over general ones
5. **Dependency Inversion Principle** - High-level modules not dependent on low-level modules

## Project Structure

- `config/` - Configuration classes for security, email, etc.
- `controller/` - MVC controllers that handle HTTP requests
- `model/` - Entity classes that map to database tables
- `repository/` - Data access interfaces
- `service/` - Business logic implementations
- `dto/` - Data Transfer Objects for form handling
- `resources/templates/` - Thymeleaf HTML templates
- `resources/static/` - Static resources (CSS, JS)

## 📁 Project Structure

```text
.
├── Dockerfile                      # Configuration to build a Docker container for your app (good for deployment)
├── README.md                       # Project documentation (you'll fill this based on my help later)
├── mvnw                            # Maven Wrapper script for Linux/macOS (runs Maven without installing it globally)
├── mvnw.cmd                        # Maven Wrapper script for Windows
├── noisy-frost-99267714_schema_only.sql # Seems like a database schema export, possibly from a tool.
├── pom.xml                         # Maven Project Object Model: Defines dependencies, plugins, build settings. Crucial.
├── schema_only.sql                 # A cleaner database schema file, likely the one you intend to use.
└── src                             # Source code directory
    ├── main                        # Main application code and resources
    │   ├── java                    # Java source code root
    │   │   └── com
    │   │       └── placementtracker  # Your application's base package
    │   │           ├── PlacementTrackerApplication.java # Main class to run the Spring Boot app (@SpringBootApplication)
    │   │           │
    │   │           ├── config        # Configuration classes for Spring Security, Email, etc.
    │   │           │   ├── AdminInitializer.java         # Likely runs on startup to ensure an admin user exists.
    │   │           │   ├── CustomAuthenticationSuccessHandler.java # Custom logic after successful login (e.g., redirect based on role).
    │   │           │   ├── EmailConfig.java            # Configuration for sending emails (SMTP server, etc.).
    │   │           │   └── SecurityConfig.java         # Core security setup: login forms, URL access rules, password encoding.
    │   │           │
    │   │           ├── controller    # --- MVC: Controllers --- Handles incoming web requests, interacts with Services, returns Views/Data.
    │   │           │   ├── AdminController.java        # Handles requests for the '/admin/**' paths.
    │   │           │   ├── AuthController.java         # Handles login, logout requests.
    │   │           │   ├── DashboardController.java    # Handles the main user dashboard view.
    │   │           │   ├── GlobalUserControllerAdvice.java # (@ControllerAdvice) Adds common data (like logged-in user) to the model for multiple controllers.
    │   │           │   ├── GroupDiscussionController.java # Handles requests related to group discussions.
    │   │           │   ├── JobController.java          # Handles requests for viewing/managing jobs (likely admin-focused).
    │   │           │   ├── JobPreferenceController.java # Handles user requests for setting job preferences.
    │   │           │   ├── LeaderboardController.java  # Handles requests for viewing leaderboards.
    │   │           │   ├── NotificationController.java # Handles requests related to user notifications.
    │   │           │   ├── NotificationControllerAdvice.java # (@ControllerAdvice) Adds notification counts or data to models globally.
    │   │           │   ├── ProgressController.java     # Handles requests for adding/viewing coding progress.
    │   │           │   ├── RedirectController.java     # Handles simple redirects (e.g., root '/' to '/login' or '/dashboard').
    │   │           │   ├── RegistrationController.java # Handles user registration requests.
    │   │           │   └── StudyGroupController.java   # Handles requests for creating/joining/managing study groups.
    │   │           │
    │   │           ├── dto           # --- Data Transfer Objects --- Plain objects to carry data between layers (e.g., Controller <-> Service, or data from forms). Prevents exposing internal Models directly.
    │   │           │   ├── DiscussionCommentDto.java
    │   │           │   ├── GroupDiscussionDto.java
    │   │           │   ├── JobDto.java
    │   │           │   ├── JobPreferenceDto.java
    │   │           │   ├── LeaderboardEntryDto.java    # DTO specifically for leaderboard display
    │   │           │   ├── LoginDto.java               # DTO for login form data
    │   │           │   ├── ProblemDto.java
    │   │           │   ├── StudyGroupDto.java
    │   │           │   ├── UserProgressDto.java
    │   │           │   └── UserRegistrationDto.java    # DTO for registration form data
    │   │           │
    │   │           ├── model         # --- MVC: Model (Entities) --- Java classes representing data structures (usually mapped to database tables using JPA @Entity).
    │   │           │   ├── DiscussionComment.java
    │   │           │   ├── GroupDiscussion.java
    │   │           │   ├── Job.java
    │   │           │   ├── JobPreference.java
    │   │           │   ├── Notification.java
    │   │           │   ├── Problem.java
    │   │           │   ├── StudyGroup.java
    │   │           │   ├── User.java                   # Represents the application user.
    │   │           │   ├── UserProgress.java
    │   │           │   └── VerificationToken.java      # Used for email verification process.
    │   │           │
    │   │           ├── repository    # --- Data Access Layer --- Interfaces (extending Spring Data JPA's JpaRepository) defining methods to interact with the database for each Model/Entity.
    │   │           │   ├── DiscussionCommentRepository.java
    │   │           │   ├── GroupDiscussionRepository.java
    │   │           │   ├── GroupLeaderboardRepository.java # Potentially a custom repository for complex leaderboard queries.
    │   │           │   ├── JobPreferenceRepository.java
    │   │           │   ├── JobRepository.java
    │   │           │   ├── NotificationRepository.java
    │   │           │   ├── ProblemRepository.java
    │   │           │   ├── StudyGroupRepository.java
    │   │           │   ├── UserProgressRepository.java
    │   │           │   ├── UserRepository.java
    │   │           │   └── VerificationTokenRepository.java
    │   │           │
    │   │           └── service       # --- Business Logic Layer --- Contains the core application logic. Services coordinate Repositories and perform operations. Often uses Interfaces and Implementations.
    │   │               ├── CustomUserDetailsService.java # Implements Spring Security's UserDetailsService to load user data for authentication.
    │   │               ├── EmailService.java           # Interface for email sending operations.
    │   │               ├── EmailServiceImpl.java       # Implementation of EmailService.
    │   │               ├── GroupDiscussionService.java / GroupDiscussionServiceImpl.java # Handles logic for discussions.
    │   │               ├── GroupLeaderboardService.java # Handles logic for calculating/fetching leaderboard data.
    │   │               ├── JobNotificationScheduler.java # (@Scheduled) Likely runs periodically to check for job matches and send notifications.
    │   │               ├── JobPreferenceService.java / JobPreferenceServiceImpl.java # Handles logic for job preferences.
    │   │               ├── JobService.java / JobServiceImpl.java # Handles logic for jobs (CRUD, fetching).
    │   │               ├── NotificationService.java / NotificationServiceImpl.java # Handles logic for notifications.
    │   │               ├── ProblemService.java / ProblemServiceImpl.java # Handles logic for problems.
    │   │               ├── ProgressService.java / ProgressServiceImpl.java # Handles logic for progress tracking.
    │   │               ├── StudyGroupService.java / StudyGroupServiceImpl.java # Handles logic for study groups.
    │   │               ├── UserService.java            # Interface for user-related operations (registration, profile updates).
    │   │               └── UserServiceImpl.java        # Implementation of UserService.
    │   │
    │   └── resources               # Non-Java files (configuration, static assets, templates)
    │       ├── application.properties  # Spring Boot configuration file (database URL, server port, email settings, etc.).
    │       ├── static                  # --- Static Web Content --- Files served directly (CSS, JavaScript, images).
    │       │   ├── css
    │       │   │   ├── admin.css
    │       │   │   └── styles.css
    │       │   └── js
    │       │       └── scripts.js
    │       └── templates               # --- MVC: Views --- Server-side templates (likely Thymeleaf given .html) processed to generate dynamic HTML.
    │           ├── admin               # Templates specific to the admin panel
    │           │   ├── dashboard.html
    │           │   ├── jobs            # Admin job management templates
    │           │   │   ├── edit.html
    │           │   │   ├── list.html
    │           │   │   └── manage.html
    │           │   ├── notifications   # Admin notification templates
    │           │   │   ├── dashboard.html
    │           │   │   └── send.html
    │           │   └── users           # Admin user management templates
    │           │       ├── list.html
    │           │       └── view.html
    │           ├── dashboard.html      # Main user dashboard template
    │           ├── groups              # Templates for study groups feature
    │           │   ├── discussion-detail.html
    │           │   ├── discussions.html
    │           │   ├── edit.html
    │           │   ├── invite.html
    │           │   ├── leaderboard.html
    │           │   ├── list.html
    │           │   └── view.html
    │           ├── jobs                # Templates for user viewing jobs
    │           │   ├── list.html
    │           │   └── view.html
    │           ├── login.html          # Login page template
    │           ├── notifications       # Templates for user notifications
    │           │   └── list.html
    │           ├── preferences         # Templates for user job preferences
    │           │   └── edit.html
    │           ├── progress            # Templates for progress tracking feature
    │           │   ├── add.html
    │           │   └── list.html
    │           ├── register-success.html # Page shown after successful registration.
    │           ├── register.html       # Registration page template.
    │           ├── verification-failure.html # Page shown if email verification fails.
    │           └── verification-success.html # Page shown after successful email verification.
    │
    └── test                        # Test code
        └── java
            └── com
                └── placementtracker
                    └── auth            # Example test package
                        ├── AuthControllerTest.java # Unit/Integration tests for AuthController.
                        └── UserServiceTest.java    # Unit/Integration tests for UserService.
