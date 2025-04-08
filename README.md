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

  coding-tracker/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── placementtracker/
│   │   │           ├── PlacementTrackerApplication.java
│   │   │           ├── config/
│   │   │           │   ├── SecurityConfig.java
│   │   │           │   └── EmailConfig.java
│   │   │           ├── controller/
│   │   │           │   ├── AuthController.java
│   │   │           │   └── RegistrationController.java
│   │   │           ├── model/
│   │   │           │   ├── User.java
│   │   │           │   └── VerificationToken.java
│   │   │           ├── repository/
│   │   │           │   ├── UserRepository.java
│   │   │           │   └── VerificationTokenRepository.java
│   │   │           ├── service/
│   │   │           │   ├── UserService.java
│   │   │           │   ├── UserServiceImpl.java
│   │   │           │   ├── EmailService.java
│   │   │           │   └── EmailServiceImpl.java
│   │   │           └── dto/
│   │   │               ├── UserRegistrationDto.java
│   │   │               └── LoginDto.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       │   ├── css/
│   │       │   │   └── styles.css
│   │       │   └── js/
│   │       │       └── scripts.js
│   │       └── templates/
│   │           ├── login.html
│   │           ├── register.html
│   │           ├── verification-success.html
│   │           └── verification-failure.html
│   └── test/
│       └── java/
│           └── com/
│               └── placementtracker/
│                   └── auth/
│                       ├── UserServiceTest.java
│                       └── AuthControllerTest.java
├── pom.xml
└── README.md
