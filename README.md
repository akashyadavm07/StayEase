# StayEase – Intelligent Hotel Guest Service Management Portal

StayEase is a web-based hotel management portal that connects hotel guests with hotel staff through a unified platform. It simplifies guest service requests, food ordering, complaint monitoring and hotel announcements.

## Features

### Guest Features
- Submit housekeeping, maintenance and room service requests.
- Receive a unique request ID after submitting a request.
- Track service requests using request ID and room number.
- Browse the hotel's available food menu.
- Select food quantities and place food orders.
- Charge food orders directly to the guest's room.
- View important hotel announcements and service alerts.

### Admin Features
- Secure admin login.
- View and manage guest service requests.
- Search requests using their unique request ID.
- Assign staff members to service requests.
- Update request statuses.
- View request statistics.
- Manage food menu items and their availability.
- View incoming food orders and update their statuses.
- Create and manage guest announcements.

### Smart Fault Radar
- Detect repeated complaints across hotel floors.
- Identify similar complaint descriptions using local text matching.
- Group potential faults by issue category and floor.
- Generate alerts when multiple similar complaints are detected.
- Help hotel management identify possible recurring infrastructure problems.

## Technologies Used

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- Thymeleaf
- HTML5
- CSS3
- JavaScript
- MySQL 8
- Maven

## Application Screenshots

### Guest Interface

![Guest Homepage](screenshots/Screenshot%202026-11-05%20134759.png)

![Guest Interface 2](screenshots/Screenshot%202026-11-05%20134815.png)

![Guest Interface 3](screenshots/Screenshot%202026-11-05%20134826.png)

### Admin Interface

![Admin Dashboard](screenshots/Screenshot%202026-11-05%20134909.png)

![Admin Interface 2](screenshots/Screenshot%202026-11-05%20134918.png)

## Database

StayEase uses MySQL to store:
- Guest service requests
- Food menu items
- Food orders and order items
- Fault alerts
- Guest announcements

## How to Run

### Prerequisites
- Java JDK 21
- MySQL Server 8
- Git

### Setup Instructions

1. Clone the repository:

   ```bash
   git clone https://github.com/akashyadavm07/StayEase.git
   ```

2. Navigate into the project:

   ```bash
   cd StayEase
   ```

3. Create a MySQL database:

   ```sql
   CREATE DATABASE stayease_db;
   ```

4. Configure your local database and admin credentials in:

   `src/main/resources/application-local.properties`

   Do not upload this file or expose your database credentials publicly.

5. Start the application using the Maven Wrapper:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

6. Open the application in your browser:

   - Guest Homepage: http://localhost:8080/
   - Request Tracking: http://localhost:8080/track
   - Admin Login: http://localhost:8080/admin/login
   - Admin Dashboard: http://localhost:8080/admin

## Project Objective

The objective of StayEase is to improve hotel service management by bringing guest requests, food ordering, administrative operations, complaint-based fault detection and guest communication together in one platform.

## Developer

**StayEase – Smart Hotel Guest Service Management Portal**

Developed as a college mini project.
