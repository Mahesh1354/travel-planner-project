CREATE DATABASE IF NOT EXISTS travel_planner_db;
USE travel_planner_db;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    profile_picture_url TEXT,
    preferred_language VARCHAR(10) DEFAULT 'en',
    preferred_currency VARCHAR(3) DEFAULT 'USD',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Trips Table
CREATE TABLE IF NOT EXISTS trips (
    trip_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    trip_name VARCHAR(100) NOT NULL,
    description TEXT,
    destination_city VARCHAR(100),
    destination_country VARCHAR(100),
    start_date DATE,
    end_date DATE,
    budget_total DECIMAL(10,2) DEFAULT 0.00,
    status ENUM('PLANNING', 'ONGOING', 'COMPLETED', 'CANCELLED') DEFAULT 'PLANNING',
    is_public BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Itinerary Items Table
CREATE TABLE IF NOT EXISTS itinerary_items (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    trip_id INT NOT NULL,
    item_date DATE NOT NULL,
    start_time TIME,
    end_time TIME,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    location_address TEXT,
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    category ENUM('FLIGHT', 'HOTEL', 'ACTIVITY', 'FOOD', 'TRANSPORT', 'OTHER') NOT NULL,
    cost DECIMAL(10,2) DEFAULT 0.00,
    booking_reference VARCHAR(100),
    status ENUM('CONFIRMED', 'PENDING', 'CANCELLED') DEFAULT 'PENDING',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (trip_id) REFERENCES trips(trip_id) ON DELETE CASCADE
);

-- Trip Members Table (Collaboration)
CREATE TABLE IF NOT EXISTS trip_members (
    member_id INT PRIMARY KEY AUTO_INCREMENT,
    trip_id INT NOT NULL,
    user_id INT NOT NULL,
    role ENUM('OWNER', 'EDITOR', 'VIEWER') DEFAULT 'VIEWER',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (trip_id) REFERENCES trips(trip_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_trip_user (trip_id, user_id)
);

-- Budget Table
CREATE TABLE IF NOT EXISTS budgets (
    budget_id INT PRIMARY KEY AUTO_INCREMENT,
    trip_id INT NOT NULL,
    category VARCHAR(50) NOT NULL,
    estimated_amount DECIMAL(10,2) NOT NULL,
    actual_amount DECIMAL(10,2) DEFAULT 0.00,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (trip_id) REFERENCES trips(trip_id) ON DELETE CASCADE
);

-- Indexes for Performance
CREATE INDEX idx_trips_user ON trips(user_id);
CREATE INDEX idx_itinerary_trip ON itinerary_items(trip_id);
CREATE INDEX idx_itinerary_date ON itinerary_items(item_date);
CREATE INDEX idx_members_trip ON trip_members(trip_id);
CREATE INDEX idx_members_user ON trip_members(user_id);
CREATE INDEX idx_budgets_trip ON budgets(trip_id);