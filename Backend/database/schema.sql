-- Truck App Database Schema
-- this is a replica of the database schema used by the Truck App backend server.

CREATE DATABASE IF NOT EXISTS truckapp;
USE truckapp;

CREATE TABLE IF NOT EXISTS users (
    id          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    phone       VARCHAR(20),
    role        ENUM('admin', 'driver', 'owner') NOT NULL DEFAULT 'driver',
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS vehicles (
    id            INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    owner_id      INT UNSIGNED NOT NULL,
    plate         VARCHAR(20)  NOT NULL UNIQUE,
    model         VARCHAR(100) NOT NULL,
    brand         VARCHAR(100) NOT NULL,
    year          YEAR,
    type          VARCHAR(50),
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- NOTE: ON DELETE CASCADE means deleting a user will also delete all their vehicles.
    CONSTRAINT fk_vehicle_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);
