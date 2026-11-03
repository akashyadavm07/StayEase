# StayEase – Smart Hotel Guest Service Management Portal

StayEase is a web-based hotel service management portal designed to simplify communication between hotel guests and hotel management.

## Features

### Guest Features
- Submit housekeeping, maintenance and amenity requests.
- Receive a unique request ID after submission.
- Track requests using request ID and room number.

### Admin Features
- Secure admin login.
- View all guest service requests.
- Search requests using their unique request ID.
- Assign staff members to requests.
- Update request statuses.
- View total, pending, in-progress and completed request statistics.

## Technologies Used
- Java 21
- Spring Boot
- Spring Security
- Thymeleaf
- HTML and CSS
- MySQL
- Maven

## Database
MySQL is used to store guest service requests, staff assignments and request statuses.

## How to Run

1. Install Java 21 and MySQL 8.
2. Create a MySQL database named `stayease_db`.
3. Configure database and admin credentials in `src/main/resources/application-local.properties`.
4. Open the project folder in a terminal.
5. Run:

   `.\mvnw.cmd spring-boot:run`

6. Open `http://localhost:8080/` in your browser.

## Application Pages
- Guest Homepage: `http://localhost:8080/`
- Request Tracking: `http://localhost:8080/track`
- Admin Login: `http://localhost:8080/admin/login`
- Admin Dashboard: `http://localhost:8080/admin`

## Project Objective
To provide an efficient platform for managing hotel guest service requests, improving request tracking and helping hotel management coordinate staff activities.
