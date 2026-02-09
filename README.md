# Job Portal Application

A comprehensive job portal application built with Spring Boot that connects recruiters with job seekers.

## Features

### Common Features
- ✅ User Registration (Recruiter/Job Seeker)
- ✅ Login/Logout Authentication
- ✅ Role-based Access Control

### Recruiter Features
- ✅ Create and manage recruiter profile
- ✅ Post new job opportunities
- ✅ View all posted jobs
- ✅ Edit and delete job postings
- ✅ View candidates who applied for jobs
- ✅ Upload profile photo

### Job Seeker Features
- ✅ Create and manage candidate profile
- ✅ Search for jobs by title, company, or location
- ✅ View job details
- ✅ Apply for jobs
- ✅ View application history
- ✅ Upload resume/CV
- ✅ Upload profile photo

## Technology Stack

- **Backend**: Spring Boot 3.5.10
- **Database**: MySQL
- **Security**: Spring Security with BCrypt password encoding
- **Template Engine**: Thymeleaf
- **ORM**: Spring Data JPA (Hibernate)
- **Build Tool**: Maven
- **Java Version**: 25

## Prerequisites

- Java 25 or higher
- MySQL Server 8.0+
- Maven 3.6+

## Database Setup

1. Install MySQL Server
2. The application will automatically create the database `db_job_portal` on first run
3. Update `application.properties` with your MySQL credentials:

```properties
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

## Running the Application

### Option 1: Using Maven
```bash
mvn spring-boot:run
```

### Option 2: Using Java
```bash
mvn clean package
java -jar target/job_portal-0.0.1-SNAPSHOT.jar
```

### Option 3: From IDE
- Run `JobPortalApplication.java` as Java Application

## Accessing the Application

1. Open browser and navigate to: `http://localhost:8080`
2. Register as a Recruiter or Job Seeker
3. Login with your credentials

### Default User Types
- **Recruiter**: For employers posting jobs
- **JobSeeker**: For candidates searching jobs

## Application Flow

### For Recruiters:
1. Register → Login → Complete Profile
2. Post New Jobs
3. View Applications
4. Manage Job Postings

### For Job Seekers:
1. Register → Login → Complete Profile
2. Search Jobs
3. Apply for Jobs
4. Track Applications

## Project Structure

```
job_portal/
├── src/main/java/com/example/job_portal/
│   ├── config/          # Security configuration
│   ├── controller/      # MVC Controllers
│   ├── entity/          # JPA Entities
│   ├── repository/      # Data Access Layer
│   └── service/         # Business Logic
├── src/main/resources/
│   ├── static/          # CSS, JS files
│   ├── templates/       # Thymeleaf HTML templates
│   └── application.properties
└── pom.xml
```

## Key Endpoints

### Public
- `/` - Home page
- `/register` - User registration
- `/login` - User login

### Recruiter (requires ROLE_Recruiter)
- `/recruiter/dashboard` - Recruiter dashboard
- `/recruiter/profile` - Manage profile
- `/recruiter/jobs` - View posted jobs
- `/recruiter/jobs/new` - Post new job
- `/recruiter/jobs/{id}` - View job and applications

### Job Seeker (requires ROLE_JobSeeker)
- `/jobseeker/dashboard` - Candidate dashboard
- `/jobseeker/profile` - Manage profile
- `/jobseeker/jobs` - Search jobs
- `/jobseeker/jobs/{id}` - View job details
- `/jobseeker/apply/{id}` - Apply for job
- `/jobseeker/applications` - View applications

## Security

- Passwords are encrypted using BCrypt
- Role-based access control
- CSRF protection enabled
- Session management

## Development Process (Step-by-Step)

As shown in the presentation:
1. ✅ Set up Spring Boot project
2. ✅ Add project template files (HTML, JS, CSS)
3. ✅ Database Entities for Users, UserTypes
4. ✅ User Registration: Repositories and Controller
5. ✅ Create profiles for Recruiter and Job Seekers
6. ✅ Add support for Skills to Job Seekers
7. ✅ Job posting and management
8. ✅ Job search and application features

## Future Enhancements

- Email notifications
- Advanced search filters
- Resume parsing
- Application status tracking
- Interview scheduling
- Analytics dashboard
- Social login integration

## License

This project is for educational purposes.

## Contact

For issues or questions, please create an issue in the repository.
# JobPortal
