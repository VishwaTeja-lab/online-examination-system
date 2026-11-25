# Online Examination System

A Java 17 and Spring Boot academic project for online multiple-choice examinations.

## Features
- Student name/email entry
- Exam catalogue and timed MCQ attempts
- Automatic scoring and result history
- Admin exam/question management and submissions dashboard
- Docker deployment configuration

## Run
```bash
mvn spring-boot:run
```
Open http://localhost:8080. Admin dashboard: http://localhost:8080/admin.

## Deploy
Use the included Dockerfile with Render. Configure a persistent database before production use.

## Academic-project security note
The demo admin interface is not production-authenticated. Add Spring Security, role-based access control, CSRF protection, and persistent database storage before exposing it publicly.
