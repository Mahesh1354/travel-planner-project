# Travel Planner Application – Backend Scope

## 1. Reference

This backend strictly follows the Software Requirements Specification (SRS)
provided for the Travel Planner Application. No extra features are added.

---

## 2. Backend Functional Scope (Mapped to SRS)

### 2.1 User Registration & Authentication (FR1, FR2)

- User registration using email and password
- Secure login
- Password recovery support
- Role support: USER, ADMIN

Backend Responsibility:

- Store user credentials securely
- Authenticate users
- Authorize API access

---

### 2.2 Trip & Itinerary Management (FR3, FR4, FR5)

- Create trip itineraries
- Edit trip details
- Delete trips
- Duplicate trips
- Share trips with collaborators
- Add destinations, activities, notes, and dates

Backend Responsibility:

- CRUD APIs for trips
- Maintain trip ownership and collaborators
- Data persistence in MySQL

---

### 2.3 Search & Booking (FR6, FR7, FR8)

- Search flights, accommodations, and activities
- Book using third-party APIs
- Store booking confirmations in itinerary

Backend Responsibility:

- Expose APIs for search
- Integrate third-party APIs
- Save booking metadata (NOT payment info)

---

### 2.4 Budget Management (FR9, FR10)

- Add estimated expenses
- Add actual expenses
- Calculate total and remaining budget

Backend Responsibility:

- Budget CRUD APIs
- Budget summary calculations

---

### 2.5 Recommendations & Tips (FR11, FR12)

- Activity and POI recommendations
- Travel tips and alerts

Backend Responsibility:

- API endpoints to fetch recommendations
- No heavy AI logic, API-based responses only

---

### 2.6 Notifications & Alerts (FR13, FR14)

- Flight updates
- Weather alerts
- Group trip updates
- Notification preferences

Backend Responsibility:

- Notification preference storage
- Trigger notifications via APIs or services

---

### 2.7 Offline Access Support (FR15)

- Allow itinerary data to be downloaded

Backend Responsibility:

- Provide APIs to fetch complete itinerary data
- Ensure data consistency

---

### 2.8 Admin Management

- Manage users
- Monitor bookings

Backend Responsibility:

- Admin-only APIs
- Role-based access control

---

## 3. Backend Modules (High-Level)

| Module Name    | Responsibility                |
| -------------- | ----------------------------- |
| Auth Module    | Login, registration, security |
| User Module    | User profile & roles          |
| Trip Module    | Trip & itinerary management   |
| Booking Module | Search & booking integration  |
| Budget Module  | Expense tracking              |
| Collaboration  | Shared trip access            |
| Notification   | Alerts & preferences          |
| Admin Module   | Admin operations              |

---

## 4. What Backend WILL Do

- Expose REST APIs
- Use Spring Boot & MySQL
- Secure APIs using authentication
- Integrate third-party services
- Follow GDPR & security guidelines

---

## 5. What Backend WILL NOT Do

- Store payment details
- Handle frontend UI logic
- Implement complex AI models
- Bypass SRS requirements
