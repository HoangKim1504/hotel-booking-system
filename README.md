# Hotel Room Booking Website

An online hotel room booking website designed to help users search for rooms, view room details, book multiple room types within a single booking, and manage their booking history.

The system also provides an Admin dashboard for managing users, room types, rooms, bookings, and monitoring system statistics.

## 1. Technologies Used

### Backend

* Java
* Spring Boot 4
* Spring REST / Spring MVC
* Spring Data MongoDB
* Spring Security
* Maven

### Frontend

* ReactJS
* JavaScript
* HTML / JSX
* CSS
* Bootstrap

### Database

* MongoDB

### Deployment

* Docker
* Docker Compose

## 2. System Architecture

The system is built using a **Monolithic Architecture**, consisting of:

```text
Frontend (ReactJS)
        |
        | REST API
        v
Backend (Spring Boot)
        |
        v
MongoDB
```

The Frontend communicates with the Backend through REST APIs.

The Backend is responsible for handling business logic, authentication, authorization, and data access from MongoDB.

## 3. Main Features

### User

* Register an account
* Log in / Log out
* Search for room types by Check-in / Check-out dates
* Search based on the number of guests
* Filter, sort, and paginate search results
* View room type details
* Add rooms to the booking cart
* Book multiple room types within a single booking
* View booking history
* View booking details
* Update personal information
* Cancel bookings based on system conditions

### Admin

* Manage room types
* Manage rooms
* Manage users
* Manage bookings
* Search, sort, and paginate data
* Update booking status
* Monitor payment status
* View system statistics
* View revenue and booking charts

## 4. Authorization

The system uses Spring Security to control access permissions.

### USER

Users are allowed to use room booking features and manage their own bookings.

### ADMIN

Admins are allowed to access the administration dashboard and perform system management operations.

Regular users are not allowed to access URLs under the `/admin` path.

Locked accounts are not allowed to log in to the system.

## 5. Project Structure

```text
project/
│
├── backend/
│   └── hotel-booking-system/
│       ├── src/
│       ├── pom.xml
│       ├── Dockerfile
│       └── docker-compose.yml
│
├── frontend/
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── ...
│
└── README.md
```

## 6. How to Run the Project

### Requirements

Install the following tools:

* Docker Desktop
* Node.js
* npm
* Git

### Run Backend and Database

Navigate to the backend directory:

```bash
cd backend/hotel-booking-system
```

Build and start Docker containers:

```bash
docker compose up --build
```

To run the containers in detached mode:

```bash
docker compose up --build -d
```

### Run Frontend

Navigate to the frontend directory:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

```bash
npm install recharts
```

Start the React application:

```bash
npm run dev
```

After the application starts successfully, open the website using the URL displayed in the frontend terminal.

## 7. Stop the System

Stop the Backend and Database:

```bash
docker compose down
```

To remove containers, networks, and volumes:

```bash
docker compose down -v
```

## 8. Rebuild the System

When Backend code is changed and Docker needs to be rebuilt:

```bash
docker compose down
docker compose build --no-cache
docker compose up
```

Alternatively:

```bash
docker compose up --build
```

## 9. API

The Backend provides REST APIs for the Frontend, including the following main API groups:

* Authentication API
* User API
* Room Type API
* Room API
* Booking API
* Cart / Booking Item API
* Admin API
* Statistics API

Data between the Frontend and Backend is mainly exchanged in JSON format.

## 10. Database

The system uses MongoDB for data storage.

Some of the main collections include:

```text
users
roomTypes
rooms
bookings
bookingItems
```

MongoDB runs through Docker Compose together with the Backend.

## 11. Project Objectives

* Build a complete hotel room booking website.
* Apply Java Spring Boot to develop REST APIs.
* Use ReactJS to build the user interface.
* Apply Spring Security for authentication and authorization.
* Use MongoDB for data management.
* Apply a Monolithic Architecture.
* Deploy the application environment using Docker and Docker Compose.
* Practice integrating Frontend, Backend, and Database components into a complete system.

## 12. Author

[Kim Tran Hoang](https://github.com/HoangKim1504)
